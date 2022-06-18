package com.ambitious.vcbestm.algorithm.estimator;

import com.ambitious.vcbestm.algorithm.info.EstimateInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 工具类
 * 提供 VocabularyEstimator 对象
 * @author Ambitious
 * @date 2022/6/14 10:14
 */
@Slf4j
public class VocabularyEstimators {

    /**
     * 默认的 Estimator 对象
     * @param curTagId 当前用户所在阶梯
     * @param wordList 可选单词列表
     * @return VocabularyEstimator
     */
    public static VocabularyEstimator getDefault(Long curTagId, List<String> wordList) {
        if (curTagId == null || wordList == null) {
            log.error("error init vocabulary estimator");
            return null;
        }
        LadderVocabularyEstimator estimator = new LadderVocabularyEstimator();
        EstimateInfo estimateInfo = new EstimateInfo(curTagId, wordList);
        estimator.setEstimateInfo(estimateInfo);
        return estimator;
    }
}
