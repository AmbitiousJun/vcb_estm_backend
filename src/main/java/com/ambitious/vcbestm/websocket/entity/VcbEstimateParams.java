package com.ambitious.vcbestm.websocket.entity;

import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimator;
import lombok.Data;

import javax.websocket.Session;

/**
 * 用户评估词汇量时的参数都存放在这个类中
 * @author Ambitious
 * @date 2022/6/14 16:32
 */
@Data
public class VcbEstimateParams {

    /**
     * websocket session 连接
     */
    private Session session;
    /**
     * 词汇量评估器，每个评估用户单独一个
     */
    private VocabularyEstimator vocabularyEstimator;
}
