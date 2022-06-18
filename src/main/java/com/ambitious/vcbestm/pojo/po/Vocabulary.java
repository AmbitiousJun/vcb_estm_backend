package com.ambitious.vcbestm.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * 词汇表
 * @author Ambitious
 * @date 2022/6/13 21:08
 */
@TableName("vocabularies")
@Data
public class Vocabulary implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 单词所属标签 JSON 数组串，如 "[1, 2, 3...]"
     */
    private String tagIds;
    /**
     * 单词
     */
    private String word;
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
