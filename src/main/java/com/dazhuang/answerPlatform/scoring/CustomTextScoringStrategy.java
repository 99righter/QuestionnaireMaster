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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 自定义测评类应用
 * 应用类型（0-得分类，1-角色测评类）'  评分策略（0-自定义，1-AI）
 */
@ScoringStrategyConfig(appType = 1,scoringStrategy = 0)
public class CustomTextScoringStrategy implements ScoringStrategy{
    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;
    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        Long appId = app.getId();
        //1.根据id查询到的题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId,appId)
        );
        List<ScoringResult> scoringResults = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId,appId)
        );
        //2.统计用户的得分
        Map<String,Integer> optionCount = new HashMap<>();
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContentDTOS = questionVO.getQuestionContent();
        //3.遍历题目列表
        for(QuestionContentDTO questionContentDTO:questionContentDTOS){
            //遍历答案列表
            for(String answer : choices){
                //遍历题目中的选项
                for(QuestionContentDTO.Option option:questionContentDTO.getOptions()){
                    if(option.getKey().equals(answer)){
//                        //未设置值的为0
//                        int score = Optional.of(option.getScore()).orElse(0);
//                        totalScore += score;
                        String result = option.getResult();
                        //如果result不存在于数组中
                        if(!optionCount.containsKey(result)){
                            optionCount.put(result,0);
                        }
                        //存在则增加计数
                        optionCount.put(result,optionCount.get(result)+1);
                    }
                }
            }
        }
        //4.遍历得分结果
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResults.get(0);
        for(ScoringResult scoringResult:scoringResults){
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(),String.class);
            int score = resultProp.stream().mapToInt(prop -> optionCount.getOrDefault(prop,0)).sum();
            if(score >maxScore){
                maxScore = score;
                maxScoringResult = scoringResult;
            }
        }

        //5.构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
//        userAnswer.setResultScore(maxScore);
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
