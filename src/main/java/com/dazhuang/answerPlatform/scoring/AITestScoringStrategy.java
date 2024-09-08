package com.dazhuang.answerPlatform.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dazhuang.answerPlatform.constant.AIGenerateSystemMessageConfig;
import com.dazhuang.answerPlatform.manager.AiManager;
import com.dazhuang.answerPlatform.model.dto.question.QuestionAnswerDTO;
import com.dazhuang.answerPlatform.model.dto.question.QuestionContentDTO;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.Question;
import com.dazhuang.answerPlatform.model.entity.UserAnswer;
import com.dazhuang.answerPlatform.model.vo.QuestionVO;
import com.dazhuang.answerPlatform.service.AppService;
import com.dazhuang.answerPlatform.service.QuestionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * AI得分类应用评价
 * 应用类型（0-得分类，1-角色测评类）'  评分策略（0-自定义，1-AI）
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {
    @Resource
    private AppService appService;
    @Resource
    private QuestionService questionService;
    @Resource
    private AiManager aiManager;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        Long appId = app.getId();
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContentDTOS = questionVO.getQuestionContent();
        /**
         *        调用ai获取结果
         */
        //对用户消息进行封装
        String userMessage = getAITestScoringUserAnswer(app, questionContentDTOS, choices);
        //AI生成
        String result = aiManager.doStableRequest(AIGenerateSystemMessageConfig.AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
        //对结果进行处理
        int startIndex = result.indexOf("{");
        int endIndex = result.lastIndexOf("}");
        String json = result.substring(startIndex, endIndex + 1);
        UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        return userAnswer;
    }

    /**
     * 创建用户选项消息队列
     *
     * @param app                 答题应用
     * @param questionContentDTOS 题目描述
     * @param choices             用户选项
     * @return
     */
    public String getAITestScoringUserAnswer(App app, List<QuestionContentDTO> questionContentDTOS, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOS = new ArrayList<>();
        for (int i = 0; i < questionContentDTOS.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOS.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOS.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOS));
        return userMessage.toString();
    }
}
