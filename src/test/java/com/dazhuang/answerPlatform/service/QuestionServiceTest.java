package com.dazhuang.answerPlatform.service;

import com.dazhuang.answerPlatform.model.dto.question.AIGenerateRequest;
import com.dazhuang.answerPlatform.model.dto.question.QuestionContentDTO;
import com.dazhuang.answerPlatform.model.entity.App;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionServiceTest {
    @Resource
    private AppService appService;
    @Resource
    private QuestionService questionService;

    @Test
    void getAIGenerateQuestion() {
        AIGenerateRequest aiGenerateRequest = new AIGenerateRequest();
        aiGenerateRequest.setAppId(1L);
        aiGenerateRequest.setQuestionNum(10);
        aiGenerateRequest.setOptionNum(2);
        Long appId = aiGenerateRequest.getAppId();
        Integer questionNum = aiGenerateRequest.getQuestionNum();
        Integer optionNum = aiGenerateRequest.getOptionNum();

        App app = appService.getById(appId);
        List<QuestionContentDTO> aiGenerateQuestion = questionService.getAIGenerateQuestion(app, questionNum, optionNum);
        System.out.println(aiGenerateQuestion);
    }
}