#!/usr/bin/env python3
"""
Script that uploads reactions to the REST API.
Author: rstular <R.Stular@student.tudelft.nl>
"""

import argparse
import io
import json
import logging
import os

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


def dir_path(string):
    if os.path.isdir(string):
        return string
    else:
        raise NotADirectoryError(string)


parser = argparse.ArgumentParser(description="Populate the service with reactions.")
parser.add_argument(
    "reactions_dir",
    help="Directory containing the reactions and images to be added to the service.",
    default="reactions/",
    type=dir_path,
)
parser.add_argument(
    "-u",
    "--api-url",
    help="URL of the REST API. (default: %(default)s)",
    default="http://localhost:8080/",
)
parser.add_argument(
    "-v",
    "--verbose",
    help="More verbose output. (default: %(default)s)",
    action="store_true",
)
args = parser.parse_args()

logging.basicConfig(
    level=logging.DEBUG if args.verbose else logging.INFO,
    format="[%(asctime)s] %(levelname)s - %(filename)s - %(message)s",
)

try:
    import requests
except ImportError:
    logging.error("Could not import 'requests' module. Please install it.")
    exit(1)

API_ENDPOINT = urllib.parse.urljoin(args.api_url, "/api/reaction")

logging.debug(f"Using API endpoint: {API_ENDPOINT}")

with open(os.path.join(args.reactions_dir, "reactions.json"), "r") as reactions_file:
    reactions = json.load(reactions_file)["reactions"]
logging.info(f"Loaded {len(reactions)} reactions.")

for reaction in reactions:
    logging.debug(f"Uploading reaction: {reaction['name']}")

    reaction_dto = {"reactionType": reaction["name"]}

    response = requests.post(
        API_ENDPOINT,
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
