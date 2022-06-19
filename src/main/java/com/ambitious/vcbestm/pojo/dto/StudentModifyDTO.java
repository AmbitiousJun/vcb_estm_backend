package com.ambitious.vcbestm.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改学生信息参数
 * @author Ambitious
 * @date 2022/6/19 9:18
 */
@Data
@ApiModel("修改学生信息参数")
public class StudentModifyDTO {

    @ApiModelProperty(value = "用户id", required = true)
    private Long userId;

    @ApiModelProperty("学生姓名")
    private String stuName;

    @ApiModelProperty("四级成绩")
    private Integer pets3Grade;

    @ApiModelProperty("六级成绩")
    private Integer pets4Grade;
}
