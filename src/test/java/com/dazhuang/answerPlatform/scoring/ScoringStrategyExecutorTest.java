package com.dazhuang.answerPlatform.scoring;

import com.dazhuang.answerPlatform.service.AppService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import com.dazhuang.answerPlatform.model.entity.App;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScoringStrategyExecutorTest {
    @Resource
    private ScoringStrategyExecutor scoringStrategyExecutor;
    @Resource
    private AppService appService;

    @Test
    void doScore() {
        App app = appService.getById(1831977270721400833L);
        List<String> choice = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            choice.add("B");
        }
        System.out.println(scoringStrategyExecutor.doScore(choice, app).toString());
    }
}