package com.ambitious.vcbestm.algorithm.info;

import lombok.Data;

import java.util.*;

/**
 * 估算词汇量过程中需要用到的数据
 * @author Ambitious
 * @date 2022/6/14 10:02
 */
@Data
public class EstimateInfo {

    /**
     * 测试总数
     */
    private Integer sum;
    /**
     * 用户当前在哪个阶梯上，随着测试进行不断改变
     */
    private Long curTagId;
    /**
     * 每次返回给用户单词进行识别之前，先暂存这个单词
     */
    private String curWord;
    /**
     * 可选单词列表，每次从中随机挑选一个返回给用户识别
     * 需要随着 curTagId 的更新而更新
     */
    private List<String> wordList;
    /**
     * 已识别单词集合
     * 用户每识别一个单词就记录一次，防止挑选重复单词
     */
    private Set<String> usedSet;
    /**
     * 不同阶梯的单词识别情况
     */
    private Map<Long, SpecTagEstimateInfo> diffTagIdentifyMap;

    @Data
    public static class SpecTagEstimateInfo {
        private Integer curSum;
        private Integer realizeNum;

        public SpecTagEstimateInfo() {
            this.curSum = 0;
            this.realizeNum = 0;
        }

        public void incrSum() {
            this.curSum++;
        }

        public void incrRealizeNum() {
            this.realizeNum++;
        }

        public void incrAll() {
            incrSum();
            incrRealizeNum();
        }
    }

    public EstimateInfo(Long curTagId, List<String> wordList) {
        this.sum = 0;
        this.curTagId = curTagId;
        this.wordList = wordList;
        this.curWord = "";
        this.usedSet = new HashSet<>();
        this.diffTagIdentifyMap = new HashMap<>(16);
    }
}
