package com.dazhuang.answerPlatform.model.dto.question;

import lombok.Data;

import java.util.List;

@Data
public class QuestionScoringAnswerDTO {
    /**
     * 答案标题
     */
    private String title;
    /**
     * 答案选项
     */
    private List<QuestionContentDTO.Option> options;
    /**
     * 用户答案
     */
    private String userAnswer;
}
