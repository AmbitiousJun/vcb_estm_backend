package com.ambitious.vcbestm.exception;

/**
 * 响应码
 * @author Ambitious
 * @date 2022/6/14 16:02
 */
public enum ErrCode {

    /**
     * 2XXX 成功 4XXX 客户端异常 5XXX 服务端异常
     */
    SUCCESS(2000, "请求成功"),

    FAILED(4000, "请求失败"),
    UN_VALID_PARAMS(4001, "参数不足"),
    USER_NOT_FOUND(4002, "用户不存在"),
    USER_IS_ESTIMATING(4003, "当前用户正在进行词汇估算"),
    UN_VALID_STU_NUM_LEN(4004, "学号长度在4到20之间"),
    UN_VALID_STU_NUM(4005, "学号不能包含特殊字符"),
    UN_VALID_PWD_LEN(4006, "密码长度在8到20之间"),
    UN_VALID_PWD(4007, "密码不能包含特殊字符"),
    STU_NAME_EXIST(4008, "姓名已存在"),
    ERROR_UNAME_OR_PWD(4009, "用户名或密码错误"),
    UN_VALID_TOKEN(4010, "token无效"),
    NEED_LOGIN(4011, "请先登录"),
    ILLEGAL_REQUEST(4012, "非法请求"),
    UN_VALID_STU_NAME_LEN(4013, "姓名长度在2到10之间"),

    SERVER_ERROR(5000, "服务器异常")
    ;
    private final Integer code;
    private final String msg;

    ErrCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
