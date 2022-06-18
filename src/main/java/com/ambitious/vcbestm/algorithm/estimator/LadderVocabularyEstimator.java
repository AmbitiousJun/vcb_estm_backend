package com.ambitious.vcbestm.algorithm.estimator;

import cn.hutool.core.util.StrUtil;
import com.ambitious.vcbestm.algorithm.info.EstimateResult;
import com.ambitious.vcbestm.algorithm.info.EstimateInfo;
import com.ambitious.vcbestm.pojo.po.VocabularyTag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 阶梯式词汇量估算器
 * @author Ambitious
 * @date 2022/6/14 10:17
 */
@Slf4j
public class LadderVocabularyEstimator implements VocabularyEstimator {

    /**
     * 用于用户评估的时候记录信息
     */
    private EstimateInfo estimateInfo;
    /**
     * 随机数生成器
     */
    private final Random random = new Random();
    /**
     * 总共测试的单词个数
     */
    private static final Integer TOTAL = 50;

    public void setEstimateInfo(EstimateInfo estimateInfo) {
        this.estimateInfo = estimateInfo;
    }

    @Override
    public String getRandomWord() {
        List<String> wordList = estimateInfo.getWordList();
        if (wordList == null || wordList.size() == 0) {
            log.error("empty word list error");
            return "";
        }
        // 随机获取一个没有测试过的单词
        String curWord = "";
        while ("".equals(curWord) || estimateInfo.getUsedSet().contains(curWord)) {
            int index = random.nextInt(wordList.size());
            curWord = wordList.get(index);
        }
        // 设置当前单词
        estimateInfo.setCurWord(curWord);
        return curWord;
    }

    @Override
    public EstimateResult know() {
        return coreHandler(1);
    }

    @Override
    public EstimateResult doNotKnow() {
        return coreHandler(2);
    }

    @Override
    public void updateStair(Long tagId, List<String> wordList) {
        if (tagId == null || wordList == null || wordList.size() == 0) {
            return;
        }
        estimateInfo.setCurTagId(tagId);
        estimateInfo.setWordList(wordList);
    }

    @Override
    public Integer getVocabularySize(List<VocabularyTag> tagList) {
        if (!TOTAL.equals(estimateInfo.getSum())) {
            log.error("can not get vocabulary size: estimate still not end");
            return 0;
        }
        if (tagList == null || tagList.size() == 0) {
            log.error("empty tag list error");
            return 0;
        }
        int ans = 0;
        // 1 获取并遍历用户在每个阶梯的作答情况
        Map<Long, EstimateInfo.SpecTagEstimateInfo> diffTagIdentifyMap = estimateInfo.getDiffTagIdentifyMap();
        for (Map.Entry<Long, EstimateInfo.SpecTagEstimateInfo> entry : diffTagIdentifyMap.entrySet()) {
            // 2 对于每个阶梯，计算预估词汇量以及权值
            Long tagId = entry.getKey();
            VocabularyTag tag = getTagInfo(tagId, tagList);
            if (tag == null) {
                log.error("tag not found");
                continue;
            }
            Integer upperBound = tag.getUpperBound();
            Integer lowerBound = tag.getLowerBound();
            if (upperBound == null || lowerBound == null) {
                log.error("tag info missing");
                continue;
            }
            EstimateInfo.SpecTagEstimateInfo specInfo = entry.getValue();
            Integer curSum = specInfo.getCurSum();
            Integer realizeNum = specInfo.getRealizeNum();
            double rate = 1.0 * realizeNum / curSum;
            int estVcbSize = lowerBound + (int) ((upperBound - lowerBound) * rate);
            double weight = 1.0 * specInfo.getCurSum() / estimateInfo.getSum();
            ans += (int) (estVcbSize * weight);
        }
        // 3 计算所有预估词汇量的加权平均值作为最终结果
        return ans;
    }

    @Override
    public Long getCurrentTagId() {
        return estimateInfo.getCurTagId();
    }

    /**
     * 从列表中找到相应的标签信息
     * @param tagId 目标标签 id
     * @param tagList 标签列表
     * @return 标签信息
     */
    private VocabularyTag getTagInfo(Long tagId, List<VocabularyTag> tagList) {
        for (VocabularyTag tag : tagList) {
            if (tagId.equals(tag.getId())) {
                return tag;
            }
        }
        return null;
    }

    /**
     * 算法核心逻辑
     * @param type 当前判断的类型 know(1) / doNotKnow(2)
     */
    private EstimateResult coreHandler(Integer type) {
        if (StrUtil.isEmpty(estimateInfo.getCurWord())) {
            log.error("estimator error: current word is empty");
            return defaultResult();
        }
        // 触发阶梯变化最少的单词个数
        int baseSpecInfoSum = 5;
        // 触发阶梯变化的临界识别率
        double baseRealizeRate = type == 1 ? 0.8 : 0.2;
        int knowType = 1;
        int doNotKnowType = 2;
        // 1. sum++
        estimateInfo.setSum(estimateInfo.getSum() + 1);
        // 2. 根据`curTagId`在`diffTagIdentifyMap`中获取到当前阶梯的测试情况
        Map<Long, EstimateInfo.SpecTagEstimateInfo> diffTagIdentifyMap = estimateInfo.getDiffTagIdentifyMap();
        EstimateInfo.SpecTagEstimateInfo specInfo = diffTagIdentifyMap.get(estimateInfo.getCurTagId());
        if (specInfo == null) {
            specInfo = new EstimateInfo.SpecTagEstimateInfo();
            diffTagIdentifyMap.put(estimateInfo.getCurTagId(), specInfo);
        }
        if (type == knowType) {
            specInfo.incrAll();
        } else {
            specInfo.incrSum();
        }
        // 3. 判断 sum 如果已经达到指定的测试总数，停止执行下面的逻辑，进行最终词汇量估算
        if (TOTAL.equals(estimateInfo.getSum())) {
            return endResult();
        }
        // 4. 当前单词添加到`usedSet`
        String curWord = estimateInfo.getCurWord();
        estimateInfo.getUsedSet().add(curWord);
        // 5. 计算用户当前阶梯的测试识别率
        Integer curSum = specInfo.getCurSum();
        Integer realizeNum = specInfo.getRealizeNum();
        double rate = 1.0 * realizeNum / curSum;
        if (type == knowType && curSum >= baseSpecInfoSum && rate >= baseRealizeRate) {
            return upStairResult();
        }
        if (type == doNotKnowType && curSum >= baseSpecInfoSum && rate <= baseRealizeRate) {
            return downStairResult();
        }
        return defaultResult();
    }

    /**
     * 通知调用层，需要将单词列表下降一个台阶
     */
    private EstimateResult downStairResult() {
        EstimateResult res = new EstimateResult();
        res.setEndFlag(Boolean.FALSE);
        res.setDir(-1);
        res.setCurTagId(estimateInfo.getCurTagId());
        return res;
    }

    /**
     * 通知调用层，需要将单词列表上升一个台阶
     */
    private EstimateResult upStairResult() {
        EstimateResult res = new EstimateResult();
        res.setEndFlag(Boolean.FALSE);
        res.setCurTagId(estimateInfo.getCurTagId());
        res.setDir(1);
        return res;
    }

    /**
     * 默认返回，curTagId 为空
     * 调用方需要继续获取单词
     */
    private EstimateResult defaultResult() {
        EstimateResult res = new EstimateResult();
        res.setEndFlag(Boolean.FALSE);
        return res;
    }

    /**
     * 评估结束
     */
    private EstimateResult endResult() {
        EstimateResult res = new EstimateResult();
        res.setEndFlag(Boolean.TRUE);
        return res;
    }
}
