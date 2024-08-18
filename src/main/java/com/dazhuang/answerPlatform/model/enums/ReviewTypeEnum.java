package com.dazhuang.answerPlatform.model.enums;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核状态类型枚举
 *
 * @author dazhuang
 */
public enum ReviewTypeEnum {
    REVIEWING("待审核",0),
    PASS("审核通过",1),
    REJECT("审核拒绝",2);

    private final String text;

    private final int value;

    ReviewTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     * @return
     */
    public List<Integer> getValues(){
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static ReviewTypeEnum getEnumByValue(int value){
        if(ObjectUtils.isEmpty(value)){
            return null;
        }
        for(ReviewTypeEnum anEnum : ReviewTypeEnum.values()){
            if(anEnum.value == value){
                return anEnum;
            }
        }
        return null;
    }

    public int getValue(){
        return value;
    }

    public String getText(){
        return text;
    }



}
