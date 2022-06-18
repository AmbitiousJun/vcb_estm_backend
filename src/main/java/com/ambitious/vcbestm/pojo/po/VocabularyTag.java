package com.ambitious.vcbestm.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Ambitious
 * @date 2022/6/13 21:13
 */
@TableName("vocabulary_tags")
@Data
public class VocabularyTag implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 标签名
     */
    private String tagName;
    /**
     * 标签等级
     */
    private Integer tagRank;
    /**
     * 词汇量范围下界
     */
    private Integer lowerBound;
    /**
     * 词汇量范围上界
     */
    private Integer upperBound;
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
     * 0 - 正常 1 - 删除
     */
    @JsonIgnore
    @TableLogic(delval = "1")
    private Integer delFlag;
}
