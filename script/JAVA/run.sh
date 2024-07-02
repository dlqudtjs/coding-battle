#!/bin/bash

room_id=$1
user_id=$2
problem_id=$3
submit_id=$4
secret_key=$5

# 컴파일
javac /code/Main.java 2> /out/compile_error.txt

# 컴파일 성공 여부 확인
if [ $? -ne 0 ]; then
    # 컴파일 오류 발생
    compile_error=$(cat /out/compile_error.txt | sed 's/"/\\"/g' | sed 's/$/\\n/' | tr -d '\n')
    container_id=$(cat /etc/hostname)

   curl -X POST -H "Content-Type: application/json" \
            -d "{\"testcaseNumber\":\"${testcase_number}\",\"roomId\":\"${room_id}\",\"userId\":\"${user_id}\",
            \"problemId\":\"${problem_id}\", \"result\":\"ERROR\", \"executionTime\":\"${execution_time}\",
            \"errorMessage\":\"${compile_error}\",\"currentTest\":\"0\", \"containerId\":\"${container_id}\",
            \"submitId\":\"${submit_id}\", \"totalTests\":\"0\",\"secretKey\":\"${secret_key}\"}" \
            https://coding-battle-mhskpios3a-du.a.run.app/v1/judges/results
    exit 1
fi

# java 프로그램 실행
cd
/script/java.sh ${room_id} ${user_id} ${problem_id} ${submit_id} ${secret_key}
```
