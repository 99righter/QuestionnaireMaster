package com.dazhuang.answerPlatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑问卷请求
 *
 */
@Data
public class AppEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 问卷名
     */
    private String appName;

    /**
     * 问卷描述
     */
    private String appDesc;

    /**
     * 问卷图标
     */
    private String appIcon;

    /**
     * 问卷类型（0-得分类，1-测评类）允许修改有点麻烦
     */
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringStrategy;



    private static final long serialVersionUID = 1L;
}