package com.dazhuang.answerPlatform.model.dto.question;

import lombok.Data;

import java.io.Serializable;
@Data
public class AIGenerateRequest implements Serializable {
    //定义序列化id
    private static final long serialVersionUID = 1L;

    /**
     * 问卷编号
     */
    private Long appId;

    /**
     * 题目数量，默认为10
     */
    private Integer questionNum = 10;

    /**
     * 选项数量，默认为2
     */
    private Integer optionNum = 2;
}
