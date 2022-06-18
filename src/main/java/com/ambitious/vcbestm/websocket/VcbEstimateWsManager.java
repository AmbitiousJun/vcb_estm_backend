package com.ambitious.vcbestm.websocket;

import cn.hutool.core.util.StrUtil;
import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimator;
import com.ambitious.vcbestm.common.SocketResult;
import com.ambitious.vcbestm.exception.ErrCode;
import com.ambitious.vcbestm.websocket.entity.VcbEstimateEvent;
import com.ambitious.vcbestm.websocket.entity.VcbEstimateParams;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket VcbEstimate 管理器
 * @author Ambitious
 * @date 2022/6/14 16:30
 */
@Slf4j
public class VcbEstimateWsManager {

    /**
     * 存放用户的连接 session 以及评估器
     */
    private static final Map<Long, VcbEstimateParams> USER_MAP = new ConcurrentHashMap<>();
    /**
     * 存放已连接上的用户的登录 token
     */
    private static final Map<Long, String> TOKEN_MAP = new ConcurrentHashMap<>();

    /**
     * 回写预估词汇量
     * @param userId 用户 id
     * @param vocabularySize 词汇量
     */
    public static void writeVocabularySizeAndClose(Long userId, Integer vocabularySize) {
        VcbEstimateParams params = getParams(userId);
        if (params == null) {
            return;
        }
        Session session = params.getSession();
        String message = SocketResult.ok(VcbEstimateEvent.RESULT, vocabularySize).json();
        writeMessageSync(session, message);
        closeSession(session);
        USER_MAP.remove(userId);
        TOKEN_MAP.remove(userId);
    }

    /**
     * 获取评估器
     * @param userId 用户 id
     * @return 评估器对象
     */
    public static VocabularyEstimator getEstimator(Long userId) {
        VcbEstimateParams params = getParams(userId);
        if (params == null) {
            return null;
        }
        return params.getVocabularyEstimator();
    }

    /**
     * 将用户从 map 中移除
     * @param userId 用户 id
     * @param token 用户登录 token
     */
    public static void removeUser(Long userId, String token) {
        if (userId == null || token == null) {
            return;
        }
        // 检查当前关闭连接的用户是否是已经合法注册的用户
        String validToken = TOKEN_MAP.get(userId);
        if (validToken == null || validToken.equals(token)) {
            USER_MAP.remove(userId);
            TOKEN_MAP.remove(userId);
        }
    }

    /**
     * 回写错误并关闭连接
     * @param userId 用户 id
     */
    public static void writeErrorAndClose(Long userId) {
        VcbEstimateParams params = getParams(userId);
        if (params == null) {
            return;
        }
        Session session = params.getSession();
        String message = SocketResult.fail(VcbEstimateEvent.ERROR, ErrCode.SERVER_ERROR).json();
        writeMessageSync(session, message);
        closeSession(session);
        USER_MAP.remove(userId);
        TOKEN_MAP.remove(userId);
    }

    /**
     * 回写单词
     * @param userId 用户 id
     * @param word 单词
     */
    public static void writeWord(Long userId, String word) {
        if (userId == null || StrUtil.isEmpty(word)) {
            return;
        }
        String message = SocketResult.ok(VcbEstimateEvent.WORD, word).json();
        writeMessageAsync(userId, message);
    }

    /**
     * 将用户信息注册到 USER_MAP 中
     * @param userId 用户 id
     * @param token 用户登录 token
     * @param session session
     * @param estimator 评估器
     * @return 是否注册成功
     */
    public static boolean register(Long userId, String token, Session session, VocabularyEstimator estimator) {
        if (USER_MAP.containsKey(userId)) {
            denyAndClose(session, ErrCode.USER_IS_ESTIMATING);
            return false;
        }
        synchronized (VcbEstimateWsManager.class) {
            if (USER_MAP.containsKey(userId)) {
                denyAndClose(session, ErrCode.USER_IS_ESTIMATING);
                return false;
            }
            VcbEstimateParams params = new VcbEstimateParams();
            params.setSession(session);
            params.setVocabularyEstimator(estimator);
            USER_MAP.put(userId, params);
            TOKEN_MAP.put(userId, token);
            return true;
        }
    }

    /**
     * 拒绝连接，返回异常信息
     * @param session session
     * @param errCode 错误码
     */
    public static void denyAndClose(Session session, ErrCode errCode) {
        String message = SocketResult.fail(VcbEstimateEvent.DENIED, errCode).json();
        writeMessageSync(session, message);
        closeSession(session);
    }

    /**
     * 异步回写消息
     * @param userId 用户 id
     * @param message 消息
     */
    public static void writeMessageAsync(Long userId, String message) {
        VcbEstimateParams params = getParams(userId);
        if (params == null) {
            return;
        }
        Session session = params.getSession();
        writeMessageAsync(session, message);
    }

    /**
     * 异步回写消息
     * @param session session
     * @param message 消息
     */
    public static void writeMessageAsync(Session session, String message) {
        if (session == null) {
            return;
        }
        RemoteEndpoint.Async remote = session.getAsyncRemote();
        remote.sendText(message);
    }

    /**
     * 同步回写消息
     * @param userId 用户 id
     * @param message 要发送的消息
     */
    public static void writeMessageSync(Long userId, String message) {
        VcbEstimateParams params = getParams(userId);
        if (params == null) {
            return;
        }
        Session session = params.getSession();
        writeMessageSync(session, message);
    }

    /**
     * 同步回写消息
     * @param session session
     * @param message 要发送的消息
     */
    public static void writeMessageSync(Session session, String message) {
        if (session == null) {
            return;
        }
        RemoteEndpoint.Basic remote = session.getBasicRemote();
        try {
            remote.sendText(message);
        } catch (IOException e) {
            log.error("send text error: ", e);
        }
    }

    /**
     * 关闭连接
     * @param session session
     */
    private static void closeSession(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 USER_MAP 中的用户数据
     * @param userId 用户 id
     * @return 用户参数，获取不到时为 null
     */
    private static VcbEstimateParams getParams(Long userId) {
        if (userId == null) {
            return null;
        }
        return USER_MAP.get(userId);
    }
}
