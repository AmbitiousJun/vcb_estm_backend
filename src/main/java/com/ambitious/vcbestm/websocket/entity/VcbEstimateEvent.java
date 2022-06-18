package com.ambitious.vcbestm.websocket.entity;

/**
 * 词汇评估事件
 * @author Ambitious
 * @date 2022/6/14 20:10
 */
public interface VcbEstimateEvent {

    /**
     * 拒绝连接
     */
    String DENIED = "denied";
    /**
     * 发送单词
     */
    String WORD = "word";
    /**
     * 出现错误
     */
    String ERROR = "error";
    /**
     * 返回词汇量预估结果
     */
    String RESULT = "result";
}
