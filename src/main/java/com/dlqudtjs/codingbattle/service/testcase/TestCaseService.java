package com.dlqudtjs.codingbattle.service.testcase;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface TestCaseService {

    ResponseDto addTestCase(Long problemId, MultipartFile testCaseFile);
}
