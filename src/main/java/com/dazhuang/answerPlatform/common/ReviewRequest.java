package com.dazhuang.answerPlatform.common;



import lombok.Data;

import java.io.Serializable;

/**
 * @author dazhuang
 */
@Data
public class ReviewRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private long id;
    /**
     * 状态 0-待审核 1-审核通过 2-审核未通过
     */
    private Integer reviewStatus;
    /**
     * 审核信息
     */
    private String reviewMessage;


}
