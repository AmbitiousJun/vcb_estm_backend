package com.ambitious.vcbestm.handler;

import com.ambitious.vcbestm.common.CommonResult;
import com.ambitious.vcbestm.exception.ErrCode;
import com.ambitious.vcbestm.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author Ambitious
 * @date 2022/6/17 17:23
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义的业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public CommonResult<?> serviceException(ServiceException e) {
        return CommonResult.fail(e.getErrCode());
    }

    /**
     * 未知异常
     */
    @ExceptionHandler(RuntimeException.class)
    public CommonResult<?> runtimeException(RuntimeException e) {
        log.error("RuntimeException: {}", e.getMessage());
        return CommonResult.fail(ErrCode.SERVER_ERROR);
    }
}
