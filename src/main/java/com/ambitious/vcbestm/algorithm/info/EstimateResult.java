package com.ambitious.vcbestm.algorithm.info;

import lombok.Data;

/**
 * 估算器处理用户的单词识别之后返回的结果
 * @author Ambitious
 * @date 2022/6/14 11:37
 */
@Data
public class EstimateResult {

    /**
     * 测试结束标识
     */
    private Boolean endFlag;
    /**
     * 用户触发上下阶梯，需要外部重新传入的标签 id
     */
    private Long curTagId;
    /**
     * 阶梯切换方向，1 上升， -1 下降
     */
    private Integer dir;
}
