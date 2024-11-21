package com.dazhuang.answerPlatform.model.dto.app;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新问卷请求
 *
 */
@Data
public class AppUpdateRequest implements Serializable {

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
     * 问卷类型（0-得分类，1-测评类）
     */
    private Integer appType;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringStrategy;

    /**
     * 审核状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;


    /**
     * 审核人 id
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private Date reviewTime;




    private static final long serialVersionUID = 1L;
}