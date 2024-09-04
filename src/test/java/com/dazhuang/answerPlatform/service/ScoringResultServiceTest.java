package com.dazhuang.answerPlatform.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.ScoringResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScoringResultServiceTest {
    @Resource
    private ScoringResultService scoringResultService;
    @Resource
    private AppService appService;

    @Test
    void listTest() {
        App app = appService.getById(1);
        scoringResultService.list(Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId,app.getId()));
    }

    @Test
    void validScoringResult() {
    }

    @Test
    void getQueryWrapper() {
    }

    @Test
    void getScoringResultVO() {
    }

    @Test
    void getScoringResultVOPage() {
    }
}