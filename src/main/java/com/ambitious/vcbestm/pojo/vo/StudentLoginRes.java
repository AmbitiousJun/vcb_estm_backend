package com.ambitious.vcbestm.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户登录之后的令牌信息
 * @author Ambitious
 * @date 2022/6/18 10:10
 */
@Data
@AllArgsConstructor
public class StudentLoginRes {

    /**
     * 用户 Id
     */
    private Long userId;
    /**
     * 登录令牌
     */
    private String token;
}
