package com.dazhuang.answerPlatform.scoring;

import com.dazhuang.answerPlatform.model.dto.app.AppQueryRequest;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.service.AppService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AITestScoringStrategyTest {
    @Resource(name = "AITestScoringStrategy")
    private ScoringStrategy scoringStrategy;
    @Resource
    private AppService appService;


    @Test
    void doScore() {
//        AppQueryRequest appQueryRequest = new AppQueryRequest();
//        Long appId = 1831977270721400833L;
//        appQueryRequest.setId(appId);
        App app = appService.getById(1831977270721400833L);
        List<String> choice = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            choice.add("A");
        }
        System.out.println(scoringStrategy.doScore(choice, app).toString());

    }

    @Test
    void getAITestScoringUserAnswer() {
    }
}