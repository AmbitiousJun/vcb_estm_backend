package com.ambitious.vcbestm.exception;

/**
 * 业务异常类
 * @author Ambitious
 * @date 2022/6/14 16:06
 */
public class ServiceException extends RuntimeException{

    private final ErrCode errCode;

    public ServiceException(ErrCode errCode) {
        this.errCode = errCode;
    }

    public ErrCode getErrCode() {
        return errCode;
    }
}
