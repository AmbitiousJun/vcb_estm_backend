package com.ambitious.vcbestm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimator;
import com.ambitious.vcbestm.algorithm.estimator.VocabularyEstimators;
import com.ambitious.vcbestm.algorithm.info.EstimateResult;
import com.ambitious.vcbestm.exception.ErrCode;
import com.ambitious.vcbestm.exception.ServiceException;
import com.ambitious.vcbestm.mapper.EstimateHistoryMapper;
import com.ambitious.vcbestm.mapper.StudentMapper;
import com.ambitious.vcbestm.mapper.VocabularyMapper;
import com.ambitious.vcbestm.mapper.VocabularyTagMapper;
import com.ambitious.vcbestm.pojo.po.EstimateHistory;
import com.ambitious.vcbestm.pojo.po.Student;
import com.ambitious.vcbestm.pojo.po.Vocabulary;
import com.ambitious.vcbestm.pojo.po.VocabularyTag;
import com.ambitious.vcbestm.service.EstimateService;
import com.ambitious.vcbestm.util.TimeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Ambitious
 * @date 2022/6/14 15:31
 */
@Service("estimateService")
@Transactional(rollbackFor = Exception.class)
public class EstimateServiceImpl implements EstimateService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private VocabularyTagMapper vocabularyTagMapper;
    @Resource
    private EstimateHistoryMapper estimateHistoryMapper;

    @Override
    public VocabularyEstimator init(Long userId) {
        if (userId == null) {
            throw new ServiceException(ErrCode.UN_VALID_PARAMS);
        }
        // 1 查询用户信息
        Student student = studentMapper.selectById(userId);
        if (student == null) {
            throw new ServiceException(ErrCode.USER_NOT_FOUND);
        }
        // 2 根据用户是否有评估历史初始化估算器
        long estimateTagId = student.getEstimateTagId();
        long tagId = estimateTagId;
        if (estimateTagId == 0) {
            // 随机挑选
            tagId = getRandomTag();
        }
        List<String> wordList = getWordList(tagId);
        return VocabularyEstimators.getDefault(tagId, wordList);
    }

    @Override
    public String getWord(VocabularyEstimator estimator) {
        if (estimator == null) {
            return "";
        }
        return estimator.getRandomWord();
    }

    @Override
    public Integer handleMessage(VocabularyEstimator estimator, String message) {
        if (estimator == null || StrUtil.isEmpty(message)) {
            return 0;
        }
        if ("know".equals(message)) {
            // 用户认识单词
            return knowWord(estimator);
        } else if("un_know".equals(message)) {
            // 用户不认识单词
            return unKnowWord(estimator);
        } else {
            // 无效信息
            return 0;
        }
    }

    @Override
    public Integer getVocabularySize(Long userId, VocabularyEstimator estimator) {
        if (estimator == null) {
            return 0;
        }
        List<VocabularyTag> tagList = vocabularyTagMapper.selectList(null);
        // 1 获取评估结果
        Integer vocabularySize = estimator.getVocabularySize(tagList);
        Long curTagId = estimator.getCurrentTagId();
        // 2 将评估结果存到数据库
        LambdaUpdateWrapper<Student> uw = new LambdaUpdateWrapper<>();
        uw.eq(Student::getId, userId);
        uw.set(Student::getEstimateTagId, curTagId);
        uw.set(Student::getVocabularySize, vocabularySize);
        uw.set(Student::getEstimateTime, TimeUtils.now());
        studentMapper.update(null, uw);
        // 3 评估结果添加到数据库中
        EstimateHistory history = new EstimateHistory();
        history.setSid(userId);
        history.setEstimateTime(TimeUtils.now());
        history.setVocabularySize(vocabularySize);
        estimateHistoryMapper.insert(history);
        return vocabularySize;
    }

    /**
     * 认识单词
     * @param estimator 评估器
     * @return 操作类型
     */
    private Integer knowWord(VocabularyEstimator estimator) {
        EstimateResult res = estimator.know();
        if (res.getEndFlag()) {
            return 2;
        }
        Long curTagId = res.getCurTagId();
        Integer dir = res.getDir();
        checkStairChange(estimator, curTagId, dir);
        return 1;
    }

    /**
     * 不认识单词
     * @param estimator 评估器
     * @return 操作类型
     */
    private Integer unKnowWord(VocabularyEstimator estimator) {
        EstimateResult res = estimator.doNotKnow();
        if (res.getEndFlag()) {
            return 2;
        }
        Long curTagId = res.getCurTagId();
        Integer dir = res.getDir();
        checkStairChange(estimator, curTagId, dir);
        return 1;
    }

    /**
     * 检查是否需要进行阶梯变换
     * @param estimator 评估器
     * @param tagId 标签 id
     * @param dir 方向
     */
    private void checkStairChange(VocabularyEstimator estimator, Long tagId, Integer dir) {
        if (tagId == null || dir == null) {
            return;
        }
        // 获取下一个标签 id
        Long nextTagId = getNextTagId(tagId, dir);
        if (nextTagId != null) {
            List<String> wordList = getWordList(nextTagId);
            estimator.updateStair(nextTagId, wordList);
        }
    }

    /**
     * 获取下一个标签 id
     * @param curTagId 当前标签 id
     * @param dir 方向 1 - 下一级 -1 - 上一级
     * @return 标签
     */
    private Long getNextTagId(Long curTagId, Integer dir) {
        // 1 查询当前标签的级别
        LambdaQueryWrapper<VocabularyTag> qw = new LambdaQueryWrapper<>();
        qw.eq(VocabularyTag::getId, curTagId);
        qw.select(VocabularyTag::getTagRank);
        int curRank = vocabularyTagMapper.selectOne(qw).getTagRank();
        // 2 级别运算
        curRank += dir;
        // 3 查询结果并返回
        qw = new LambdaQueryWrapper<>();
        qw.eq(VocabularyTag::getTagRank, curRank);
        qw.select(VocabularyTag::getId);
        VocabularyTag tag = vocabularyTagMapper.selectOne(qw);
        if (tag == null) {
            return null;
        }
        return tag.getId();
    }

    /**
     * 随机获取标签
     * @return 标签 id
     */
    private Long getRandomTag() {
        // 1 查询所有标签
        List<VocabularyTag> tagList = vocabularyTagMapper.selectList(null);
        // 2 随机挑选并返回
        int index = new Random().nextInt(tagList.size());
        return tagList.get(index).getId();
    }

    /**
     * 根据标签 id 获取词汇列表
     * @param tagId 标签 id
     * @return 词汇列表
     */
    private List<String> getWordList(Long tagId) {
        LambdaQueryWrapper<Vocabulary> qw = new LambdaQueryWrapper<>();
        qw.like(Vocabulary::getTagIds, tagId);
        qw.select(Vocabulary::getWord);
        List<Vocabulary> list = vocabularyMapper.selectList(qw);
        return list.stream().map(Vocabulary::getWord).collect(Collectors.toList());
    }
}
