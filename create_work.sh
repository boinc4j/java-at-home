#!/usr/bin/env bash

START_SUM=${1}
CHUNK_SIZE=${2}
NUM_OF_JOBS=${3}

cd boinc-project

NEXT_SUM=${START_SUM}
for JOB in $(seq 0 ${NUM_OF_JOBS}); do
  ./bin/create_work -appname ${HEROKU_APP_NAME} \
      -wu_name sum_${NEXT_SUM}_${JOB} \
      -wu_template templates/app_in \
      -result_template templates/app_out \
      -command_line "${NEXT_SUM} ${CHUNK_SIZE}"
  NEXT_SUM=$((${NEXT_SUM} + ${CHUNK_SIZE}))
done