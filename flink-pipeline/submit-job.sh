#!/usr/bin/env bash

JOB_CLASS_NAME="$1"
JM_CONTAINER=$(docker ps --filter name=jobmanager --format={{.ID}})
docker cp build/libs/flink-pipeline-0.0.1-SNAPSHOT-all.jar "${JM_CONTAINER}":/job.jar
docker exec -t -i "${JM_CONTAINER}" flink run -d -c "${JOB_CLASS_NAME}" /job.jar