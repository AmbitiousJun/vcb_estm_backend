package com.ambitious.vcbestm.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Ambitious
 * @date 2022/6/13 20:38
 */
@Data
@TableName("students")
public class Student implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学号
     */
    private String stuNum;
    /**
     * 姓名
     */
    private String stuName;
    /**
     * 密码
     */
    private String password;
    /**
     * 四级成绩
     */
    @TableField("PETS3_grade")
    private Integer pets3Grade;
    /**
     * 六级成绩
     */
    @TableField("PETS4_grade")
    private Integer pets4Grade;
    /**
     * 最后评估时间
     */
    private String estimateTime;
    /**
     * 预估词汇量
     */
    private Integer vocabularySize;
    /**
     * 预估词汇量在哪个标签范围内
     */
    private Long estimateTagId;
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
