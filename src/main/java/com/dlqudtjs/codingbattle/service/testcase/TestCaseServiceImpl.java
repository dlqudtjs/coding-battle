package com.dlqudtjs.codingbattle.service.testcase;

import static com.dlqudtjs.codingbattle.common.exception.testcase.TestCaseErrorCode.TEST_CASE_FILE_FORMAT_ERROR;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.testcase.TestCaseErrorCode;
import com.dlqudtjs.codingbattle.entity.problem.Problem;
import com.dlqudtjs.codingbattle.repository.problem.ProblemRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public ResponseDto addTestCase(Long problemId, MultipartFile testCaseFile) {
        // zip 파일이 아닌 경우 예외 발생
        if (!isZipFile(testCaseFile)) {
            custom4XXException(TEST_CASE_FILE_FORMAT_ERROR);
        }

        Map<String, String> inputFiles = new HashMap<>();
        Map<String, String> outputFiles = new HashMap<>();
        splitZipIntoInputAndOutputFiles(testCaseFile, inputFiles, outputFiles);

        // input, output 파일의 개수가 일치하지 않으면 예외 발생
        if (inputFiles.size() != outputFiles.size()) {
            custom4XXException(TEST_CASE_FILE_FORMAT_ERROR);
        }

        // db에 저장하는 로직
        for (String fileName : inputFiles.keySet()) {
            String inputContent = inputFiles.get(fileName);
            String outputContent = outputFiles.get(fileName);

            if (inputContent == null || outputContent == null) {
                custom4XXException(TEST_CASE_FILE_FORMAT_ERROR);
            }

            saveTestCaseToRepository(problemId, inputContent, outputContent);
        }

        return ResponseDto.builder()
                .status(200)
                .message("테스트케이스 추가 성공")
                .build();
    }

    private void saveTestCaseToRepository(Long problemId, String input, String output) {
        Problem problem = problemRepository.findById(problemId).orElse(null);

        if (problem == null) {
            custom4XXException(TEST_CASE_FILE_FORMAT_ERROR);
        }

//        ProblemTestCase testCase = ProblemTestCase.builder()
//                .problem(problem)
//                .input(input)
//                .output(output)
//                .build();

        //problemTestCaseRepository.save(testCase);
    }

    // zip 파일을 input, output 파일로 분리
    private void splitZipIntoInputAndOutputFiles(
            MultipartFile file,
            Map<String, String> inputFiles,
            Map<String, String> outputFiles
    ) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream());
            ZipEntry nextEntry = zipInputStream.getNextEntry();

            while (nextEntry != null) {
                String fileName = nextEntry.getName();
                if (!nextEntry.isDirectory()) {
                    String content = StreamUtils.copyToString(zipInputStream, StandardCharsets.UTF_8);
                    if (fileName.endsWith(".in")) {
                        // 확장자를 제거하여 파일명을 추출
                        String key = fileName.substring(0, fileName.length() - 3);
                        inputFiles.put(key, content);
                    } else if (fileName.endsWith(".out")) {
                        // 확장자를 제거하여 파일명을 추출
                        String key = fileName.substring(0, fileName.length() - 4);
                        outputFiles.put(key, content);
                    }
                }

                nextEntry = zipInputStream.getNextEntry();
            }
        } catch (IOException e) {
            custom4XXException(TEST_CASE_FILE_FORMAT_ERROR);
        }
    }

    private boolean isZipFile(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename()).endsWith(".zip");
    }

    private void custom4XXException(TestCaseErrorCode errorCode) {
        throw new Custom4XXException(
                errorCode.getMessage(),
                errorCode.getStatus()
        );
    }
}
