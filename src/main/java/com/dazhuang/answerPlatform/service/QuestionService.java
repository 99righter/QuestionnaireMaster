package com.dazhuang.answerPlatform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dazhuang.answerPlatform.model.dto.question.QuestionContentDTO;
import com.dazhuang.answerPlatform.model.dto.question.QuestionQueryRequest;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.Question;
import com.dazhuang.answerPlatform.model.vo.QuestionVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface QuestionService extends IService<Question> {

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * AI生成题目
     *
     * @param app         APP
     * @param questionNum 题目数量
     * @param optionNum   选项数量
     */
    List<QuestionContentDTO> getAIGenerateQuestion(App app, int questionNum, int optionNum);

    /**
     * AI 流式生成题目
     *
     * @param app         APP
     * @param questionNum 题目数量
     * @param optionNum   选项数量
     * @return 流式对象
     */
    SseEmitter getSseAIGenerateQuestion(App app, int questionNum, int optionNum);
}
