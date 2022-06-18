package com.ambitious.vcbestm.filter;

/**
 * 使用 ThreadLocal 保存登录用户的 id
 * @author Ambitious
 * @date 2022/6/18 16:57
 */
public class LocalUser {

    private static final ThreadLocal<Long> LOCAL_USER_ID = new ThreadLocal<>();

    /**
     * 设置值
     * @param uid 登录用户 id
     */
    public static void set(Long uid) {
        LOCAL_USER_ID.remove();
        LOCAL_USER_ID.set(uid);
    }

    /**
     * 获取值
     */
    public static Long get() {
        return LOCAL_USER_ID.get();
    }
}
