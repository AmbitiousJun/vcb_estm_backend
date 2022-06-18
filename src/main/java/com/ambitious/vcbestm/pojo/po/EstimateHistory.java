package com.ambitious.vcbestm.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * 词汇评估历史
 * @author Ambitious
 * @date 2022/6/18 16:33
 */
@Data
@TableName("estimate_histories")
public class EstimateHistory implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学生表主键
     */
    private Long sid;
    /**
     * 词汇评估时间
     */
    private String estimateTime;
    /**
     * 预估词汇量
     */
    private Integer vocabularySize;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;
    /**
     * 0 - 正常， 1 - 删除
     */
    @JsonIgnore
    @TableLogic(delval = "1")
    private Integer delFlag;
}
