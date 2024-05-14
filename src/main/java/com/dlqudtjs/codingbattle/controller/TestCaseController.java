package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.service.testcase.TestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;

    @PostMapping("/v1/problem/testcase/{problemId}")
    public ResponseEntity<ResponseDto> addTestcase(
            @PathVariable("problemId") Long problemId,
            @RequestPart("testCaseFile") MultipartFile testCaseFile
    ) {
        ResponseDto responseDto = testCaseService.addTestCase(problemId, testCaseFile);
        
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }
}
