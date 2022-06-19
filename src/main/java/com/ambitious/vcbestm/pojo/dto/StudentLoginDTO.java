package com.ambitious.vcbestm.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户登录参数
 * @author Ambitious
 * @date 2022/6/19 9:34
 */
@Data
@ApiModel("用户登录参数")
public class StudentLoginDTO {

    @ApiModelProperty(value = "学号", required = true)
    private String stuNum;

    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
