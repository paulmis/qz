#!/usr/bin/env python3
"""
Script that creates questions from activities in the activity-bank
Author: rstular <R.Stular@student.tudelft.nl>
"""

import argparse
import logging
from typing import Any

import urllib.request
import urllib.error

class Response:
    """
    Wrapper for response from post_data()
    """

    def __init__(self, resp_data: bytes, status_code: int):
        """Initialize a new Response object

        Args:
            resp_data (bytes): response data
            status_code (int): response status code
        """
        self.data: bytes = resp_data
        self.status_code: int = status_code

def put(url: str, put_data: Any, headers=None) -> Response:
    """
    PUT data to `url`

    Args:
        url (str): URL to PUT to.
        put_data (Any): Data to put
        headers (dict[str, str]): HTTP headers to send with request

    Returns:
        Response: Response object
    """
    # if data is not in bytes, convert to it to utf-8 bytes
    if headers is None:
        headers = {}
    bindata = put_data if type(put_data) == bytes else put_data.encode("utf-8")
    # need Request to pass headers
    req = urllib.request.Request(url, bindata, headers, method="PUT")
    req.add_header("Content-Type", "application/json")
    try:
        response = urllib.request.urlopen(req)
    except urllib.error.HTTPError as e:
        return Response(b"", e.code)
    return Response(response.read(), response.status)


def status_is_ok(status_code: int) -> bool:
    """
    Return True if status code is in the 2xx range

    Args:
        status_code (int): HTTP status code

    Returns:
        bool: True if status code is in the 2xx range
    """
    return 200 <= status_code < 300

parser = argparse.ArgumentParser(
    description="Populate the Quizzz database with questions."
)
parser.add_argument(
    "-u",
    "--api-url",
    help="URL of the REST API. (default: %(default)s)",
    default="http://localhost:8080/",
)
parser.add_argument(
    "-c",
    "--count",
    help="Number of questions to generate. (default: %(default)s)",
    type=int,
    default=20,
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
    format="%(asctime)s - %(levelname)s - %(message)s",
)

if args.count < 1:
    logging.error("Count must be at least 1")
    exit(1)

for i in range(args.count):
    if (i + 1) % 10 == 0:
        logging.info(f"Creating question {i + 1} of {args.count}")
    else:
        logging.debug(f"Creating question {i + 1} of {args.count}")

    resp = put(f"{args.api_url}api/question/mc", b"")
    if not status_is_ok(resp.status_code):
        logging.error(f"Failed to create question {i + 1}")
        logging.error(f"Status code: {resp.status_code}")
        exit(1)

    logging.debug(f"Created question {i + 1} of {args.count}")
