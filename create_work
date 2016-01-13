#!/usr/bin/env bash

START_SUM=${1}
CHUNK_SIZE=${2}
NUM_OF_JOBS=${3}

export PATH=/app/.apt/usr/sbin:/app/.apt/usr/bin:$PATH

cd boinc-project

NEXT_SUM=${START_SUM}
for JOB in $(seq 0 ${NUM_OF_JOBS}); do
  echo "Creating work unit for ${NEXT_SUM} to $((${NEXT_SUM} + ${CHUNK_SIZE} - 1))"
  ./bin/create_work -appname ${HEROKU_APP_NAME} \
      -wu_name sum_${NEXT_SUM}_${JOB} \
      -wu_template templates/app_in \
      -result_template templates/app_out \
      -command_line "${NEXT_SUM} ${CHUNK_SIZE}" \
      in.txt
  NEXT_SUM=$((${NEXT_SUM} + ${CHUNK_SIZE}))
done

echo "Done creating work!"