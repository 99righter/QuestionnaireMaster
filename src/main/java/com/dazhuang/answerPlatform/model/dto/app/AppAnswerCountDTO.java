package com.dazhuang.answerPlatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppAnswerCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 问卷id、
     */
    private Long appId;
    /**
     * 问卷回答次数
     */
    private Long AnswerCount;
}
