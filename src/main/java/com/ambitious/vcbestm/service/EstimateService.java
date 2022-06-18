package com.ambitious.vcbestm.service;

import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimator;

/**
 * 词汇量评估业务接口
 * @author Ambitious
 * @date 2022/6/14 15:29
 */
public interface EstimateService {

    /**
     * 初始化估算器
     * @param userId 用户 id
     * @return 估算器
     */
    VocabularyEstimator init(Long userId);

    /**
     * 获取一个单词
     * @param estimator 评估器
     * @return 单词
     */
    String getWord(VocabularyEstimator estimator);

    /**
     * 对用户的选择进行评估
     * @param estimator 评估器
     * @param message 用户发送的消息
     * @return 操作类型：0 - 不操作， 1 - 获取下一个单词， 2 - 获取估算结果
     */
    Integer handleMessage(VocabularyEstimator estimator, String message);

    /**
     * 获取评估结果
     * @param userId 用户 id
     * @param estimator 评估器
     * @return 预估词汇量
     */
    Integer getVocabularySize(Long userId, VocabularyEstimator estimator);
}
