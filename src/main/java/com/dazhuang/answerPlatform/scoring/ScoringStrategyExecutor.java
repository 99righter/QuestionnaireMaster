package com.dazhuang.answerPlatform.scoring;

import com.dazhuang.answerPlatform.common.ErrorCode;
import com.dazhuang.answerPlatform.exception.BusinessException;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScoringStrategyExecutor {
    @Resource
    private List<ScoringStrategy> scoringStrategyList;

    public UserAnswer doScore(List<String> choices, App app){
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();
        if(appType ==null || appScoringStrategy ==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未找到匹配的评分策略");
        }
        for(ScoringStrategy scoringStrategy:scoringStrategyList){
            //根据注解获取策略
            if(scoringStrategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)){
                ScoringStrategyConfig scoringStrategyConfig = scoringStrategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                if(scoringStrategyConfig.appType() ==appType && scoringStrategyConfig.scoringStrategy() == appScoringStrategy){
                    return scoringStrategy.doScore(choices,app);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未找到匹配的评分策略");
    }
}
