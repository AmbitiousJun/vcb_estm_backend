package com.ambitious.vcbestm.common;

import com.ambitious.vcbestm.exception.ErrCode;
import lombok.Data;

/**
 * 通用返回结果集
 * @author Ambitious
 * @date 2022/6/17 16:58
 */
@Data
public class CommonResult<T> {

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String msg;
    /**
     * 响应数据
     */
    private T data;

    private CommonResult() {}

    public static CommonResult<?> ok() {
        return ok(null);
    }

    public static <T>CommonResult<T> ok(T data) {
        return ok(ErrCode.SUCCESS.getMsg(), data);
    }

    public static <T>CommonResult<T> ok(String msg, T data) {
        CommonResult<T> res = new CommonResult<>();
        res.setCode(ErrCode.SUCCESS.getCode());
        res.setMsg(msg);
        res.setData(data);
        return res;
    }

    public static CommonResult<?> fail() {
        return fail(ErrCode.FAILED);
    }

    public static CommonResult<?> fail(ErrCode errCode) {
        CommonResult<?> res = new CommonResult<>();
        res.setMsg(errCode.getMsg());
        res.setCode(errCode.getCode());
        res.setData(null);
        return res;
    }
}
