package com.ambitious.vcbestm.service;

import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Scanner;

/**
 * @author Ambitious
 * @date 2022/6/14 22:28
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EstimateServiceTest {

    @Resource
    private EstimateService estimateService;

    @Test
    void testEstimate() {
        VocabularyEstimator estimator = estimateService.init(1L);
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < 50; i++) {
            String word = estimateService.getWord(estimator);
            System.out.println(word);
            int input = scanner.nextInt();
            String msg;
            if (input == 1) {
                msg = "know";
            } else {
                msg = "un_know";
            }
            estimateService.handleMessage(estimator, msg);
        }
        Integer vocabularySize = estimateService.getVocabularySize(1L, estimator);
        System.out.printf("vocabularySize: %d\n", vocabularySize);
    }

    @Test
    void testMath() {
        int a = 10;
        double b = 0.3;
        int ans = (int) (a * b);
        System.out.println(ans);
    }
}