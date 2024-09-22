package com.dazhuang.answerPlatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppAnswerResultCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 答案名称
     */
    private String resultName;
    /**
     * 答案描述
     */
    private Integer resultCount;
}
