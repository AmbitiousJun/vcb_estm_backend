package com.ambitious.vcbestm.algorithm.estimator;

import com.ambitious.vcbestm.algorithm.info.EstimateResult;
import com.ambitious.vcbestm.pojo.po.VocabularyTag;

import java.util.List;

/**
 * 定义词汇估算器接口方法
 * @author Ambitious
 * @date 2022/6/14 9:59
 */
public interface VocabularyEstimator {

    /**
     * 从当前阶梯的单词列表中随机挑选一个单词
     * @return 单词
     */
    String getRandomWord();

    /**
     * 认识当前单词
     * @return 评估结果
     */
    EstimateResult know();

    /**
     * 不认识当前单词
     * @return 评估结果
     */
    EstimateResult doNotKnow();

    /**
     * 更新当前阶梯
     * @param tagId 标签 id
     * @param wordList 词汇列表
     */
    void updateStair(Long tagId, List<String> wordList);

    /**
     * 测试结束，估算词汇量
     * @return 预估词汇量
     * @param tagList 所有词汇标签列表
     */
    Integer getVocabularySize(List<VocabularyTag> tagList);

    /**
     * 获取当前标签 id
     * @return 标签 id
     */
    Long getCurrentTagId();
}
