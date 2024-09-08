package com.dazhuang.answerPlatform.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dazhuang.answerPlatform.constant.AIGenerateSystemMessageConfig;
import com.dazhuang.answerPlatform.manager.AiManager;
import com.dazhuang.answerPlatform.model.dto.question.QuestionAnswerDTO;
import com.dazhuang.answerPlatform.model.dto.question.QuestionContentDTO;
import com.dazhuang.answerPlatform.model.dto.question.QuestionScoringAnswerDTO;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.Question;
import com.dazhuang.answerPlatform.model.entity.UserAnswer;
import com.dazhuang.answerPlatform.model.vo.QuestionVO;
import com.dazhuang.answerPlatform.service.AppService;
import com.dazhuang.answerPlatform.service.QuestionService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AI得分类应用评价
 * 应用类型（0-得分类，1-角色测评类）'  评分策略（0-自定义，1-AI）
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 1)
public class AIScoreScoringStrategy implements ScoringStrategy {
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
        //todo 对我的选项做映射，相当于直接告诉ai我的选项的答案，而不是只是单纯告诉ai我的选项，而是告诉ai我选项的值
        List<String> reflectionAnswer = new ArrayList<>();
        //首先要遍历我的选项列表、
        for (QuestionContentDTO questionContentDTO : questionContentDTOS) {
            int i = questionContentDTOS.indexOf(questionContentDTO);
            if (i >= choices.size()) {
                break;
            }
            String answer = choices.get(i);
            //遍历题目中的选项
            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                if (option.getKey().equals(answer)) {
                    //未设置值的为0
                    reflectionAnswer.add(option.getValue());
                }
            }
        }
        String userMessage = getAITestScoringUserAnswer(app, questionContentDTOS, choices);
        //AI生成
        String result = aiManager.doStableRequest(AIGenerateSystemMessageConfig.AI_SCORE_SCORING_SYSTEM_MESSAGE_TEST, userMessage);
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
        List<QuestionScoringAnswerDTO> questionAnswerDTOS = new ArrayList<>();
        for (int i = 0; i < questionContentDTOS.size(); i++) {
            QuestionScoringAnswerDTO questionAnswerDTO = new QuestionScoringAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOS.get(i).getTitle());
            List<QuestionContentDTO.Option> options = questionContentDTOS.get(i).getOptions();
            questionAnswerDTO.setOptions(options);
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOS.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOS));
        return userMessage.toString();
    }
}
