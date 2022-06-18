package com.ambitious.vcbestm;

import cn.hutool.core.util.StrUtil;
import com.ambitious.vcbestm.mapper.StudentMapper;
import com.ambitious.vcbestm.mapper.VocabularyMapper;
import com.ambitious.vcbestm.mapper.VocabularyTagMapper;
import com.ambitious.vcbestm.pojo.po.Student;
import com.ambitious.vcbestm.pojo.po.Vocabulary;
import com.ambitious.vcbestm.pojo.po.VocabularyTag;
import com.ambitious.vcbestm.util.TimeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Ambitious
 * @date 2022/6/13 20:59
 */
@SpringBootTest
@Slf4j
public class DbTest {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private VocabularyTagMapper vocabularyTagMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;

    private static final Long curTag = 7L;

    @Test
    void testInsert() {
        Student student = new Student();
        student.setStuNum("12138");
        student.setStuName("测试用户");
        student.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        student.setPets3Grade(468);
        student.setPets4Grade(0);
        int row = studentMapper.insert(student);
        Assertions.assertEquals(1, row);
    }

    @Test
    void testDelete() {
        int row = studentMapper.deleteById(1);
        Assertions.assertEquals(1, row);
    }

    @Test
    void testModifyTagTime() {
        List<VocabularyTag> tagList = vocabularyTagMapper.selectList(null);
        for (VocabularyTag tag : tagList) {
            tag.setCreateTime(TimeUtils.now());
            vocabularyTagMapper.updateById(tag);
        }
    }

    @Test
    void testInsertWords() {
        String path = "word_data/pets_5.txt";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        Assertions.assertNotNull(is);
        Scanner scanner = new Scanner(is);
        while (scanner.hasNext()) {
            String word = scanner.nextLine();
            if (StrUtil.isEmpty(word)) {
                continue;
            }
            Vocabulary vcb = findWord(word);
            if (vcb == null) {
                insertNewWord(word);
            } else {
                updateWordTags(vcb);
            }
        }
    }

    private void updateWordTags(Vocabulary vcb) {
        Gson gson = new Gson();
        List<Long> curTags = gson.fromJson(vcb.getTagIds(), new TypeToken<List<Long>>() {
        }.getType());
        boolean flag = false;
        for (Long tag : curTags) {
            if (curTag.equals(tag)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            return;
        }
        curTags.add(curTag);
        vcb.setTagIds(gson.toJson(curTags));
        vocabularyMapper.updateById(vcb);
    }

    private void insertNewWord(String word) {
        Vocabulary vcb = new Vocabulary();
        List<Long> idList = new ArrayList<>();
        idList.add(curTag);
        Gson gson = new Gson();
        vcb.setTagIds(gson.toJson(idList));
        vcb.setWord(word);
        vocabularyMapper.insert(vcb);
    }

    private Vocabulary findWord(String word) {
        LambdaQueryWrapper<Vocabulary> qw = new LambdaQueryWrapper<>();
        qw.eq(Vocabulary::getWord, word);
        return vocabularyMapper.selectOne(qw);
    }
}
