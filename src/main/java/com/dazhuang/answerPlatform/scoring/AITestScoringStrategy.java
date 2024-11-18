package com.dazhuang.answerPlatform.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dazhuang.answerPlatform.common.ErrorCode;
import com.dazhuang.answerPlatform.constant.AIGenerateSystemMessageConfig;
import com.dazhuang.answerPlatform.constant.CaffeineConstant;
import com.dazhuang.answerPlatform.exception.BusinessException;
import com.dazhuang.answerPlatform.manager.AiManager;
import com.dazhuang.answerPlatform.manager.CacheKeyManager;
import com.dazhuang.answerPlatform.model.dto.question.QuestionAnswerDTO;
import com.dazhuang.answerPlatform.model.dto.question.QuestionContentDTO;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.Question;
import com.dazhuang.answerPlatform.model.entity.UserAnswer;
import com.dazhuang.answerPlatform.model.vo.QuestionVO;
import com.dazhuang.answerPlatform.service.AppService;
import com.dazhuang.answerPlatform.service.QuestionService;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AI测评类问卷评价
 * 问卷类型（0-得分类，1-角色测评类）'  评分策略（0-自定义，1-AI）
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {
    @Resource
    private AppService appService;
    @Resource
    private QuestionService questionService;
    @Resource
    private AiManager aiManager;
    @Resource
    private CacheKeyManager cacheKeyManager;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        Long appId = app.getId();
        String choiceStr = JSONUtil.toJsonStr(choices);
        String cache_key = cacheKeyManager.buildCacheKey(appId, choiceStr);
        //判断缓存中是否存在，若存在直接返回缓存中的结果即可
        String answerJson = CaffeineConstant.ANSWER_MAP.getIfPresent(cache_key);
        if (StrUtil.isNotBlank(answerJson)) {
            UserAnswer userAnswer = JSONUtil.toBean(answerJson, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(choiceStr);
            return userAnswer;
        }
        //定义锁
        RLock lock = redissonClient.getLock(CaffeineConstant.AI_LOCK_KEY + cache_key);
        try {
            //竞争锁
            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
            //如果没有抢到锁
            if (!res) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "当前请求过多，请稍后再试");
            }
            //如果抢到锁,就执行后续业务逻辑
            //在缓存中不存在的话则调用ai进行生成
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
            //将AI产生的结果放进缓存中
            CaffeineConstant.ANSWER_MAP.put(cache_key, json);
            //构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(JSONUtil.toJsonStr(choices));
            return userAnswer;
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后再试");
        } finally {
            //如果是本进程
            if (lock != null && lock.isLocked()) {
                //如果是本进程
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 创建用户选项消息队列
     *
     * @param app                 答题问卷
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
