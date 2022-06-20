package com.ambitious.vcbestm;

import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimator;
import com.ambitious.vcbestm.entity.BatchResult;
import com.ambitious.vcbestm.service.EstimateService;
import com.google.gson.Gson;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * 对词汇估计算法进行批处理测试
 * @author Ambitious
 * @date 2022/6/20 8:50
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BatchTest {

    @Resource
    private EstimateService estimateService;
    private static final String FILE_NAME = "C:/Users/Ambitious/Desktop/batch_test_info.txt";

    /**
     * 一个完整的词汇估算模拟流程
     * @return 评估结果，包含词汇列表、预估词汇量信息
     */
    private BatchResult baseAlgorithmHandler() {
        long testUserId = 1L;
        BatchResult res = new BatchResult();
        // 初始化评估器
        VocabularyEstimator estimator = estimateService.init(testUserId);
        for (;;) {
            // 1 获取一个单词
            String word = estimateService.getWord(estimator);
            // 2 随机选择认识 / 不认识
            String msg = Math.random() > 0.5 ? "know" : "un_know";
            // 3 记录当前选择结果
            BatchResult.SingleWordResult wordRes = new BatchResult.SingleWordResult(word, msg.equals("know"));
            res.getWordList().add(wordRes);
            if (msg.equals("know")) {
                res.incrKnowNum();
            }
            // 4 判断是否评估结束
            int event = estimateService.handleMessage(estimator, msg);
            if (event == 2) {
                break;
            }
        }
        int vocabularySize = estimateService.getVocabularySize(testUserId, estimator);
        res.setVocabularySize(vocabularySize);
        return res;
    }

    /**
     * 往文件中写入一行数据
     * @param content 要写入的内容
     */
    private void writeLineToFile(String content) {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists() && !file.createNewFile()) {
                log.error("无法创建新文件");
                return;
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(content + "\n");
            bw.close();
        } catch (Exception e) {
            log.error("写入文件出错: {}", e.getMessage());
        }
    }

    @Test
    void batchTest() {
        // 测试 50 次
        int t = 50;
        Gson gson = new Gson();
        while (t-- > 0) {
            BatchResult batchResult = baseAlgorithmHandler();
            String listContent = "词汇测试列表: " + gson.toJson(batchResult.getWordList());
            log.info(listContent);
            String knowContent = "认识单词个数: " + batchResult.getKnowNum();
            writeLineToFile(knowContent);
            log.info(knowContent);
            writeLineToFile(listContent);
            String sizeContent = "预估词汇量: " + batchResult.getVocabularySize();
            log.info(sizeContent);
            writeLineToFile(sizeContent);
            writeLineToFile("");
        }
    }
}
