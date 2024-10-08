package com.dazhuang.answerPlatform.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dazhuang.answerPlatform.model.dto.question.QuestionContentDTO;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.Question;
import com.dazhuang.answerPlatform.model.entity.ScoringResult;
import com.dazhuang.answerPlatform.model.entity.UserAnswer;
import com.dazhuang.answerPlatform.model.vo.QuestionVO;
import com.dazhuang.answerPlatform.service.QuestionService;
import com.dazhuang.answerPlatform.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 自定义打分雷应用
 * 应用类型（0-得分类，1-角色测评类）'  评分策略（0-自定义，1-AI）
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        Long appId = app.getId();
        //1.根据id查询到的题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResults = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );
        System.out.println(scoringResults);
        //2.统计用户的得分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContentDTOS = questionVO.getQuestionContent();
        // todo 修改评分，应该同时遍历题目和选项，然后为了避免用户题目未作答就提交答案，需要先遍历用户答案
        for (QuestionContentDTO questionContentDTO : questionContentDTOS) {
            //遍历答案列表
//            for (String answer : choices) {
//                System.out.println("当前遍历的题目编号为" + questionContentDTOS.indexOf(questionContentDTO) + "----当前遍历的答案编号为" + choices.indexOf(answer));
//                //遍历题目中的选项
//                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
//                    if (option.getKey().equals(answer)) {
//                        //未设置值的为0
//                        int score = Optional.of(option.getScore()).orElse(0);
//                        totalScore += score;
//                    }
//                }
//            }
            //只打了5题，那么这个时候，第五题之后，也就是索引为5的就没有答案会发生数组越界了
            int i = questionContentDTOS.indexOf(questionContentDTO);
            if (i >= choices.size()) {
                break;
            }
            String answer = choices.get(i);
            //遍历题目中的选项
            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                if (option.getKey().equals(answer)) {
                    //未设置值的为0
                    int score = Optional.of(option.getScore()).orElse(0);
                    totalScore += score;
                }
            }
        }
//        for (String answer : choices) {
//            int i = choices.indexOf(answer);
//            QuestionContentDTO questionContentDTO = questionContentDTOS.get(i);
//            //遍历每题的选项
//            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
//                if (option.getKey().equals(answer)) {
//                    //未设置值的为0
//                    int score = Optional.of(option.getScore()).orElse(0);
//                    totalScore += score;
//                }
//            }
//        }
        //3.遍历题目列表
//        for (QuestionContentDTO questionContentDTO : questionContentDTOS) {
//            //遍历答案列表
//            for (String answer : choices) {
//                System.out.println("当前遍历的题目编号为" + questionContentDTOS.indexOf(questionContentDTO) + "----当前遍历的答案编号为" + choices.indexOf(answer));
//                //遍历题目中的选项
//                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
//                    if (option.getKey().equals(answer)) {
//                        //未设置值的为0
//                        int score = Optional.of(option.getScore()).orElse(0);
//                        totalScore += score;
//                    }
//                }
//            }
//        }
        //4.遍历得分结果
        ScoringResult maxScoringResult = scoringResults.get(0);
        for (ScoringResult scoringResult : scoringResults) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                maxScoringResult = scoringResult;
                break;
            }
        }
        //5.构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultScore(totalScore);
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
