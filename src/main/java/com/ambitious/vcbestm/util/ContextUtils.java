package com.ambitious.vcbestm.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * 存放 Spring 上下文
 * @author Ambitious
 * @date 2022/6/17 12:41
 */
@Slf4j
public class ContextUtils {

    private static ApplicationContext applicationContext;

    public static void initContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }

    /**
     * 从容器中获取 bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
