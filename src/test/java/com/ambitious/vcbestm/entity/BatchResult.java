package com.ambitious.vcbestm.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 批处理测试结果
 * @author Ambitious
 * @date 2022/6/20 8:52
 */
@Data
public class BatchResult {

    /**
     * 测试的单词列表以及认识情况
     */
    private List<SingleWordResult> wordList = new ArrayList<>();
    /**
     * 认识的单词个数
     */
    private Integer knowNum = 0;
    /**
     * 预估词汇量
     */
    private Integer vocabularySize;

    public void incrKnowNum() {
        this.knowNum++;
    }

    @Data
    public static class SingleWordResult {

        private String word;
        private Boolean know;
        public SingleWordResult(String word, boolean know) {
            this.word = word;
            this.know = know;
        }
    }
}
