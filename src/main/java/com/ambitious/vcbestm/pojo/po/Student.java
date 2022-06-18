package com.ambitious.vcbestm.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Ambitious
 * @date 2022/6/13 20:38
 */
@Data
@ApiModel("用户实体")
@TableName("students")
public class Student implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("学号")
    private String stuNum;

    @ApiModelProperty("姓名")
    private String stuName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("四级成绩")
    @TableField("PETS3_grade")
    private Integer pets3Grade;

    @ApiModelProperty("六级成绩")
    @TableField("PETS4_grade")
    private Integer pets4Grade;

    @ApiModelProperty("最后评估时间")
    private String estimateTime;

    @ApiModelProperty("预估词汇量")
    private Integer vocabularySize;

    @ApiModelProperty("预估词汇量在哪个标签范围内")
    private Long estimateTagId;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    @ApiModelProperty("0 - 正常，1 - 删除")
    @JsonIgnore
    @TableLogic(delval = "1")
    private Integer delFlag;
}
