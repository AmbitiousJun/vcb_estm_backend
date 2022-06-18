package com.ambitious.vcbestm.service;

import com.ambitious.vcbestm.pojo.po.Student;
import com.ambitious.vcbestm.pojo.vo.StudentLoginRes;

import javax.servlet.http.HttpSession;

/**
 * 用户业务
 * @author Ambitious
 * @date 2022/6/14 16:37
 */
public interface StudentService {

    /**
     * 查询单个用户
     * @param userId 用户 id
     * @return 用户信息
     */
    Student findById(Long userId);

    /**
     * 用户注册
     * @param student 用户对象，需要（学号、姓名、密码）三个属性
     */
    void register(Student student);

    /**
     * 用户登录
     * @param student 用户对象，需要（学号、密码）两个属性
     * @param session session会话
     * @return 登录令牌
     */
    StudentLoginRes login(Student student, HttpSession session);

    /**
     * 修改用户信息
     * 可修改属性: 姓名、四级成绩、六级成绩
     * @param student 用户对象（ id 必传）
     */
    void modifyInfo(Student student);
}
