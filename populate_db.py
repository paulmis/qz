#!/usr/bin/env python3
"""
Script that populates the database with activities from the activity-bank and reactions
Author: rstular <R.Stular@student.tudelft.nl>
"""

import argparse
import json
import logging
import io
import os

import requests
import urllib.parse


def status_is_ok(status_code: int) -> bool:
    """
    Return True if status code is in the 2xx range

    Args:
        status_code (int): HTTP status code

    Returns:
        bool: True if status code is in the 2xx range
    """
    return 200 <= status_code < 300


def activity_map_func(activity):
    """Map between activity format in the JSON and ActivityDTO format

    Args:
        activity (dict): Activity from the activity-bank JSON

    Returns:
        dict: Activity in the ActivityDTO format
    """

    dto = {
        "description": activity["title"],
        "cost": activity["consumption_in_wh"],
        # We don't care about URL fragments
        "source": urllib.parse.urldefrag(activity["source"])[0],
        "icon": activity["image_path"],
    }

    # Verify the cost value (and its validity)
    if args.allow_negative == False and activity["consumption_in_wh"] < 0:
        # If the const value is invalid, skip the activity
        return None

    # Verify the title length - if it's too long, truncate it
    if len(dto["description"]) > args.description_len:
        logging.warning(
            f"Activity {activity['id']} has a description longer than {args.description_len} characters. Truncating."
        )
        logging.warning(f"Description: {dto['description']}")
        dto["description"] = dto["description"][: args.description_len]

    # Verify the source length - if it's too long, truncate it
    if len(dto["source"]) > args.source_len:
        logging.warning(
            f"Activity {activity['id']} has a source longer than {args.source_len} characters. Truncating."
        )
        logging.warning(f"Source: {dto['source']}")
        dto["source"] = dto["source"][: args.source_len]

    return dto


def dir_path(string):
    if os.path.isdir(string):
        return string
    else:
        raise NotADirectoryError(string)


parser = argparse.ArgumentParser(
    description="Populate the Quizzz database with activities and reactions."
)
parser.add_argument(
    "activities_dir",
    help="Directory containing the activities and images to be added to the database.",
    type=dir_path,
)
parser.add_argument(
    "reactions_dir",
    help="Directory containing the reactions and images to be added to the database.",
    type=dir_path,
)
parser.add_argument(
    "-u",
    "--api-url",
    help="URL of the REST API. (default: %(default)s)",
    default="http://localhost:8080/",
)
parser.add_argument(
    "-c",
    "--chunk-size",
    help="Number of activities to be added in one chunk. (default: %(default)s)",
    type=int,
    default=20,
)
parser.add_argument(
    "-v",
    "--verbose",
    help="More verbose output. (default: %(default)s)",
    action="store_true",
)
auth_group = parser.add_argument_group(
    "Authentication details",
    "Configure the authentication details for the API endpoint",
)
auth_group.add_argument(
    "-a",
    "--auth-enabled",
    help="Enable authentication. (default: %(default)s)",
    action="store_true",
)
auth_group.add_argument(
    "-e",
    "--email",
    help="E-mail to use for authentication. (default: %(default)s)",
    type=str,
    default="population@example.com",
)
auth_group.add_argument(
    "-p",
    "--password",
    help="Password to use for authentication. (default: %(default)s)",
    type=str,
    default="population",
)
dto_group = parser.add_argument_group(
    "ActivityDTO details",
    "Configure the ActivityDTO details to be used when adding activities",
)
dto_group.add_argument(
    "-d",
    "--description-len",
    help="Maximum length of description. (default: %(default)s)",
    type=int,
    default=255,
)
dto_group.add_argument(
    "-s",
    "--source-len",
    help="Maximum length of source. (default: %(default)s)",
    type=int,
    default=2048,
)
dto_group.add_argument(
    "-n",
    "--allow-negative",
    help="Allow negative cost/consumption values. (default: %(default)s)",
    action="store_true",
)
args = parser.parse_args()

logging.basicConfig(
    level=logging.DEBUG if args.verbose else logging.INFO,
    format="[%(asctime)s] %(levelname)s - %(filename)s - %(message)s",
)

# Define all extra headers to be sent with the request
HEADERS = {}
if args.auth_enabled:
    logging.info("Authentication enabled")

    AUTH_ENDPOINT = urllib.parse.urljoin(args.api_url, "/api/auth/login")
    logging.debug(f'Logging in user "{args.email}"...')

    # Perform the request to the correct endpoint
    result = requests.post(
        AUTH_ENDPOINT,
        json={"email": args.email, "username": args.email, "password": args.password},
    )
    # If the request failed, exit with an error
    if not status_is_ok(result.status_code):
        logging.error(f'Failed to login user "{args.email}".')
        logging.error(f"Status code: {result.status_code}")
        logging.error(f'Response: "{result.text}"')
        exit(1)

    # If the request succeeded, load the JWT token
    logging.debug(f'Successfully logged in user "{args.email}".')
    token = result.text
    HEADERS["Authorization"] = f"Bearer {token}"
else:
    logging.info("Authentication disabled")

# Get the API endpoint URL
API_ENDPOINT = urllib.parse.urljoin(args.api_url, "/api/activity/batch/images")

logging.debug(f"Using API endpoint: {API_ENDPOINT}")

# Load activities from the provided JSON
with open(os.path.join(args.activities_dir, "activities.json"), "r") as activities_file:
    activities_raw = json.load(activities_file)
logging.info(f"Loaded {len(activities_raw)} activities.")
# Map the activities into correct format
activities_map = map(activity_map_func, activities_raw)
activities = [x for x in activities_map if x is not None]
logging.debug(f"Successfully mapped activities into ActivityDTO format.")

# Add activities in chunks
for i in range(0, len(activities), args.chunk_size):
    logging.debug(f"Adding activities {i} to {i + args.chunk_size}")

    # Get the activities to add
    chunk = activities[i : i + args.chunk_size]
    # POST the activities
    # If we need to upload images load them first
    files = [
        (
            "activities",
            (
                "blob",
                io.StringIO(json.dumps(chunk)),
                "application/json",
            ),
        )
    ]
    for activity in chunk:
        # Open the image file
        files.append(
            (
                "images",
                (
                    activity["icon"],
                    open(os.path.join(args.activities_dir, activity["icon"]), "rb"),
                ),
            )
        )
    # Send the POST request with the images
    resp = requests.post(
        API_ENDPOINT,
        headers=HEADERS,
        files=files,
    )

    # If the request failed, exit with an error
    if not status_is_ok(resp.status_code):
        logging.error(f"Failed to add activities {i} to {i + args.chunk_size}")
        logging.error(f"Status code: {resp.status_code}")
        logging.error(f'Response: "{resp.text}"')
        exit(1)

    logging.info(f"Added activities {i} to {i + args.chunk_size}")

logging.info("Uploading reactions")

API_ENDPOINT = urllib.parse.urljoin(args.api_url, "/api/reaction")

with open(os.path.join(args.reactions_dir, "reactions.json"), "r") as reactions_file:
    reactions = json.load(reactions_file)["reactions"]
logging.info(f"Loaded {len(reactions)} reactions.")

for reaction in reactions:
    logging.debug(f"Uploading reaction: {reaction['name']}")

    reaction_dto = {"reactionType": reaction["name"]}

    response = requests.post(
        API_ENDPOINT,
        headers=HEADERS,
        files=[
            (
                "reaction",
                (
                    "blob",
                    io.StringIO(json.dumps(reaction_dto)),
                    "application/json",
                ),
            ),
            (
                "image",
                (
                    reaction["image"],
                    open(os.path.join(args.reactions_dir, reaction["image"]), "rb"),
                ),
            ),
        ],
    )
    if not status_is_ok(response.status_code):
        logging.error(f"Failed to add reaction {reaction['name']}")
        logging.error(f"Status code: {response.status_code}")
        logging.error(f'Response: "{response.text}"')
        exit(1)

    logging.info(f"Successfully added reaction {reaction['name']}.")

logging.info("Done.")
