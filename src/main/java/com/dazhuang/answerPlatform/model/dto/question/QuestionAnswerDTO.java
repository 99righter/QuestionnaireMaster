package com.dazhuang.answerPlatform.model.dto.question;

import lombok.Data;

@Data
public class QuestionAnswerDTO {
    /**
     * 答案标题
     */
    private String title;
    /**
     * 用户答案
     */
    private String userAnswer;
}
