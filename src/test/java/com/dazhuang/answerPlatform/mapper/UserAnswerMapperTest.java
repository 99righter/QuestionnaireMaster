package com.dazhuang.answerPlatform.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserAnswerMapperTest {
    @Resource
    private UserAnswerMapper userAnswerMapper;

    @Test
    void getAppAnswerResultCountGroupByResult() {
        userAnswerMapper.getAppAnswerResultCountGroupByResult(1L);
    }
}