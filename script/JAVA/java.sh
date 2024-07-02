#!/bin/bash

room_id=$1
user_id=$2
problem_id=$3
submit_id=$4
secret_key=$5

# /testcase/ 디렉토리에 있는 모든 .in 파일들을 배열로 저장
input_files=(/testcase/*.in)
total_tests=${#input_files[@]}
container_id=$(cat /etc/hostname)

# 모든 .in 파일에 대해 반복
for ((i=0; i<total_tests; i++)); do
    input_file=${input_files[$i]}

    # 문제 번호 추출
    testcase_number=$(basename "${input_file%.in}")

    # 출력 파일명 생성
    output_file="/out/${testcase_number}.out"
    expected_output_file="/testcase/${testcase_number}.out"

    # 시작 시간
    start_time=$(date +%s%N)

    # Java 프로그램 실행
    java -cp /code Main < "$input_file" > "$output_file" 2> /out/runtime_error.txt

    # 종료 시간
    end_time=$(date +%s%N)

    # 실행 시간 계산 (밀리초로 변환)
    execution_time=$((($end_time - $start_time) / 1000000))

    # Java 프로그램이 정상적으로 실행되었는지 확인
    if [ $? -eq 0 ]; then
        # 실제 출력 파일과 기대 출력 파일을 비교 (줄바꿈 무시)
        if diff -q -w "$output_file" "$expected_output_file" > /dev/null; then
            result="PASS"
        else
            result="FAIL"
            runtime_error=""
        fi
    else
        result="ERROR"
        runtime_error=$(cat /out/runtime_error.txt)
    fi

    # 현재 테스트케이스 인덱스 (1부터 시작하도록 +1)
    current_test=$((i + 1))

    # 결과를 서버로 전송
       curl -X POST -H "Content-Type: application/json" \
         -d "{\"testcaseNumber\":\"${testcase_number}\",\"roomId\":\"${room_id}\",\"userId\":\"${user_id}\",
         \"problemId\":\"${problem_id}\", \"result\":\"${result}\", \"executionTime\":\"${execution_time}\",
         \"errorMessage\":\"${runtime_error}\",\"currentTest\":\"${current_test}\", \"containerId\":\"${container_id}\",
         \"submitId\":\"${submit_id}\", \"totalTests\":\"${total_tests}\",\"secretKey\":\"${secret_key}\"}" \
         https://coding-battle-mhskpios3a-du.a.run.app/v1/judges/results

    # 결과와 실행 시간을 파일에 기록
    echo -e "${result}\nExecution Time: ${execution_time}" > "/out/${testcase_number}.txt"

    # 결과가 FAIL 또는 ERROR인 경우 스크립트 종료
    if [ "$result" == "FAIL" ] || [ "$result" == "ERROR" ]; then
        break
    fi
done
