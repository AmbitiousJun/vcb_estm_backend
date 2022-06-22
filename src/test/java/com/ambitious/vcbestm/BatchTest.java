package com.ambitious.vcbestm;

import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimator;
import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimators;
import com.ambitious.vcbestm.algorithm.info.EstimateResult;
import com.ambitious.vcbestm.mapper.VocabularyMapper;
import com.ambitious.vcbestm.mapper.VocabularyTagMapper;
import com.ambitious.vcbestm.pojo.po.Vocabulary;
import com.ambitious.vcbestm.pojo.po.VocabularyTag;
import com.ambitious.vcbestm.service.EstimateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * 对词汇估计算法进行批处理测试
 * @author Ambitious
 * @date 2022/6/20 8:50
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BatchTest {

    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private VocabularyTagMapper vocabularyTagMapper;

    /**
     * 存不同层的单词数据
     */
    private static Map<Long, List<String>> WORD_DATA;
    /**
     * 模拟用户认识的单词
     */
    private static Set<String> KNOW_WORDS;
    /**
     * 数据库单词数据个数
     */
    private static final Integer MAX_WORD_SIZE = 14586;
    /**
     * 批处理结果存放路径
     */
    private static final String FILE_NAME = "C:/Users/Ambitious/Desktop/batch_test_info.txt";

    /**
     * 从数据库获取单词数据存放在 WORD_DATA 中
     * @param n 单词个数
     */
    private void initData(int n) {
        WORD_DATA = new HashMap<>(16);
        // 1 查出数据库中所有的单词数据
        LambdaQueryWrapper<Vocabulary> qw = new LambdaQueryWrapper<>();
        qw.select(Vocabulary::getWord, Vocabulary::getTagIds);
        List<Vocabulary> vocabularies = vocabularyMapper.selectList(null);
        // 2 将数据分别填充到 WORD_DATA 不同层级的 List 中
        Gson gson = new Gson();
        Random random = new Random();
        while (n-- > 0) {
            Vocabulary vocabulary = vocabularies.get(random.nextInt(vocabularies.size()));
            // 将标签id json 解析成 List
            List<Long> tags = gson.fromJson(vocabulary.getTagIds(), new TypeToken<List<Long>>() {
            }.getType());
            // 根据不同 tag 把单词存到 WORD_DATA 的不同位置
            for (Long tag : tags) {
                List<String> wordList = WORD_DATA.computeIfAbsent(tag, k -> new ArrayList<>());
                wordList.add(vocabulary.getWord());
            }
        }
    }

    /**
     * 从 WORD_DATA 中，分层抽样出用户认识的单词
     * @param n 抽样个数
     */
    private void initKnowData(int n) {
        KNOW_WORDS = new HashSet<>();
        // 总份数
        int tot = 45;
        // 当前标签所占比例
        int num = 9;
        Random random = new Random();
        for (long i = 1L; i <= 9L; i++) {
            List<String> wordList = WORD_DATA.get(i);
            if (wordList == null) {
                num--;
                continue;
            }
            double rate = 1.0 * num / tot;
            // 计算当前层级需要抽多少个单词
            int t = (int) (n * rate);
            while (t-- > 0) {
                KNOW_WORDS.add(wordList.get(random.nextInt(wordList.size())));
            }
        }
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
        // 获取 100 个测试结果
        int T = 100;
        while (T-- > 0) {
            // 1 设置总词库 A 的大小 N
            Random random = new Random();
            int N = random.nextInt(MAX_WORD_SIZE) + 1;
            log.info("单词总个数: {}", N);
            writeLineToFile("单词总个数: " + N);
            initData(N);
            // 2 设置认识的词汇个数 n，并从 WORD_DATA 中分层抽 n 个
            int n = random.nextInt(N) + 1;
            initKnowData(n);
            log.info("用户认识的单词个数 n: {}", n);
            writeLineToFile("用户认识的单词个数 n: " + n);
            // 3 初始化评估器，从第 1 层开始测评
            VocabularyEstimator estimator = VocabularyEstimators.getDefault(1L, WORD_DATA.getOrDefault(1L, new ArrayList<>()));
            if (estimator == null) {
                return;
            }
            // 4 开始测试，总共测 50 个单词
            int t = 50;
            while (t-- > 0) {
                String word = estimator.getRandomWord();
                EstimateResult result;
                // KNOW_WORDS 包含的单词就认为用户认识
                if (KNOW_WORDS.contains(word)) {
                    result = estimator.know();
                } else {
                    result = estimator.doNotKnow();
                }
                if (result.getEndFlag()) {
                    break;
                }
                Long curTagId = result.getCurTagId();
                Integer dir = result.getDir();
                if (curTagId != null && dir != null) {
                    // 阶梯变化
                    if (curTagId + dir >= 1L &&
                        curTagId + dir <= 9L &&
                        WORD_DATA.getOrDefault(curTagId + dir, new ArrayList<>()).size() > 0) {
                        estimator.updateStair(curTagId + dir, WORD_DATA.get(curTagId + dir));
                    }
                }
            }
            // 5 获取最终测试结果
            List<VocabularyTag> tagList = vocabularyTagMapper.selectList(null);
            int m = estimator.getVocabularySize(tagList);
            log.info("算法评估的词汇量 m: {}", m);
            writeLineToFile("算法评估的词汇量 m: " + m);
            writeLineToFile("");
        }
    }
}
