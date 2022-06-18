package com.ambitious.vcbestm.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间相关工具类
 * @author Ambitious
 * @date 2022/6/13 20:57
 */
public class TimeUtils {

    /**
     * 时间格式化字符串
     */
    private static final String TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前时间
     * @return 格式化字符串
     */
    public static String now() {
        return DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN).format(LocalDateTime.now());
    }

    /**
     * 返回一个 几天后 的日期
     * @param days 具体多少天
     * @return java.util.Date
     */
    public static Date dateAfterDays(long days) {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(days);
        return Date.from(ZonedDateTime.of(dateTime, ZoneId.systemDefault()).toInstant());
    }
}
