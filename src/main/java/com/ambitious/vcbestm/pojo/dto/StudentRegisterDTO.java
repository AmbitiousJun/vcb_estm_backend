package com.ambitious.vcbestm.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户注册参数
 * @author Ambitious
 * @date 2022/6/19 9:25
 */
@Data
@ApiModel("用户注册参数")
public class StudentRegisterDTO {

    @ApiModelProperty(value = "学号", required = true, notes = "4 到 20 位")
    private String stuNum;

    @ApiModelProperty(value = "姓名", required = true, notes = "2 到 10 位")
    private String stuName;

    @ApiModelProperty(value = "密码", required = true, notes = "8 到 20 位")
    private String password;
}
