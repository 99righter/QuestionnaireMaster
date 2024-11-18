package com.dazhuang.answerPlatform.model.dto.app;

import com.dazhuang.answerPlatform.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询问卷请求
 *

 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppQueryRequest extends PageRequest implements Serializable {
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
     * 创建用户 id
     */
    private Long userId;



    /**
     * 搜索词
     */
    private String searchText;



    private static final long serialVersionUID = 1L;
}