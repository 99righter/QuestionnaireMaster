package com.dazhuang.answerPlatform.scoring;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScoringStrategyConfig {
    /**
     *问卷类型（0-得分类，1-角色测评类）'  评分策略（0-自定义，1-AI）
     * @return
     */
    int appType();
    int scoringStrategy();
}
