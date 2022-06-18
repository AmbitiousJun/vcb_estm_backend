package com.ambitious.vcbestm.websocket;

import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimator;
import com.ambitious.vcbestm.exception.ErrCode;
import com.ambitious.vcbestm.pojo.po.Student;
import com.ambitious.vcbestm.service.EstimateService;
import com.ambitious.vcbestm.service.StudentService;
import com.ambitious.vcbestm.util.ContextUtils;
import com.ambitious.vcbestm.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.security.PublicKey;

/**
 * 使用 websocket 维持连接，评估用户词汇量
 * @author Ambitious
 * @date 2022/6/14 16:16
 */
@Slf4j
@Component
@ServerEndpoint("/vcb-estimate/{userId}/{token}")
public class VcbEstimate {

    private StudentService studentService;
    private EstimateService estimateService;
    private PublicKey jwtPublicKey;

    /**
     * 手动从容器中获取需要的 bean
     */
    public void init() {
        studentService = ContextUtils.getBean(StudentService.class);
        estimateService = ContextUtils.getBean(EstimateService.class);
        jwtPublicKey = ContextUtils.getBean(PublicKey.class);
    }

    @OnOpen
    public void onOpen(@PathParam("userId") Long userId,
                       @PathParam("token") String token,
                       Session session) {
        log.info("建立 WS 连接成功，userId: {}", userId);
        if (studentService == null || estimateService == null || jwtPublicKey == null) {
            init();
        }
        // 1 判断用户是否存在
        Student student = studentService.findById(userId);
        if (student == null) {
            VcbEstimateWsManager.denyAndClose(session, ErrCode.USER_NOT_FOUND);
            return;
        }
        // 2 判断用户是否登录 (JWT)
        try {
            Long tokenUserId = JwtUtils.getUserIdFromToken(token, jwtPublicKey);
            if (!userId.equals(tokenUserId)) {
                VcbEstimateWsManager.denyAndClose(session, ErrCode.UN_VALID_TOKEN);
                return;
            }
        } catch (Exception e) {
            // token 转换异常
            VcbEstimateWsManager.denyAndClose(session, ErrCode.UN_VALID_TOKEN);
            return;
        }
        // 3 初始化评估器
        VocabularyEstimator estimator = estimateService.init(userId);
        // 4 注册当前用户到程序中
        if (!VcbEstimateWsManager.register(userId, token,  session, estimator)) {
            return;
        }
        // 5 返回单词
        String word = estimateService.getWord(estimator);
        VcbEstimateWsManager.writeWord(userId, word);
    }

    @OnMessage
    public void onMessage(@PathParam("userId") Long userId, String message) {
        log.info("接收到来自userId: {}的消息: {}", userId, message);
        // "know" || "un_know"
        // 1 拿到评估器
        VocabularyEstimator estimator = VcbEstimateWsManager.getEstimator(userId);
        if (estimator == null) {
            return;
        }
        // 2 调用业务进行处理
        int res = estimateService.handleMessage(estimator, message);
        // 3 根据业务层返回结果输出信息
        if (res == 1) {
            // 获取单词返回
            String word = estimateService.getWord(estimator);
            VcbEstimateWsManager.writeWord(userId, word);
        } else if (res == 2) {
            // 获取评估数据返回
            int vocabularySize = estimateService.getVocabularySize(userId, estimator);
            VcbEstimateWsManager.writeVocabularySizeAndClose(userId, vocabularySize);
        }
        // 无效的信息，不处理
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId,
                        @PathParam("token") String token) {
        log.info("连接关闭, userId: {}", userId);
        VcbEstimateWsManager.removeUser(userId, token);
    }

    @OnError
    public void onError(@PathParam("userId") Long userId, Throwable throwable) {
        log.info("出现错误, userId: {}, error: {}", userId, throwable);
        VcbEstimateWsManager.writeErrorAndClose(userId);
    }
}
