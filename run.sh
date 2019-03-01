#!/bin/bash

# This is a helper script to run the application
# After modifying it to your project, you can use it as following:
#
# ./run.sh LinkListener
# ./run.sh SearchResultsServer
# ./run.sh TwitterListener

# Configure AWS environment variable for SDK API
export AWS_ACCESS_KEY_ID=EXAMPLEXAMPLE
export AWS_SECRET_ACCESS_KEY=EXAMPLEXAMPLE
export AWS_DEFAULT_REGION=eu-west-1

# Run the java program with different main as user provided
# Also send the correct system properties (don't forget backslash before newline)
java -cp target/TwitterFeeder-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -D config.twitter.consumer.key=1234 \
    -D config.twitter.consumer.secret=1234 \
    -D config.twitter.access.token=1234 \
    -D config.twitter.access.secret=1234 \
    -D config.twitter.track=EXAMPLE \
    -D config.sqs.url=SQS_URL \
    ... \
    ... \
    il.ac.colman.cs.$1
