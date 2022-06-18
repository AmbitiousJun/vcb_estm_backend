package com.ambitious.vcbestm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ambitious.vcbestm.exception.ErrCode;
import com.ambitious.vcbestm.exception.ServiceException;
import com.ambitious.vcbestm.mapper.StudentMapper;
import com.ambitious.vcbestm.pojo.po.Student;
import com.ambitious.vcbestm.pojo.vo.StudentLoginRes;
import com.ambitious.vcbestm.service.StudentService;
import com.ambitious.vcbestm.util.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.security.PrivateKey;
import java.util.regex.Pattern;

/**
 * @author Ambitious
 * @date 2022/6/14 16:38
 */
@Service("studentService")
@Transactional(rollbackFor = Exception.class)
public class StudentServiceImpl implements StudentService {

    @Resource
    private StudentMapper studentMapper;
    @Resource
    private PrivateKey jwtPrivateKey;

    @Override
    public Student findById(Long userId) {
        if (userId == null) {
            return null;
        }
        Student student = studentMapper.selectById(userId);
        if (student == null) {
            return null;
        }
        student.setPassword("********");
        return student;
    }

    @Override
    public void register(Student student) {
        if (student == null) {
            throw new ServiceException(ErrCode.UN_VALID_PARAMS);
        }
        String stuNum = student.getStuNum();
        String stuName = student.getStuName();
        String password = student.getPassword();
        // 非空校验
        if (!StrUtil.isAllNotEmpty(stuName, stuNum, password)) {
            throw new ServiceException(ErrCode.UN_VALID_PARAMS);
        }
        // 学号至少长度 4 到 20 位
        if (stuNum.length() < 4 || stuNum.length() > 20) {
            throw new ServiceException(ErrCode.UN_VALID_STU_NUM_LEN);
        }
        // 学号不包含特殊字符
        String reg="[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“'。，、？]";
        if (Pattern.compile(reg).matcher(stuNum).find()) {
            throw new ServiceException(ErrCode.UN_VALID_STU_NUM);
        }
        // 密码为 8 到 20 位
        if (password.length() < 8 || password.length() > 20) {
            throw new ServiceException(ErrCode.UN_VALID_PWD_LEN);
        }
        // 密码不能包含特殊字符
        if (Pattern.compile(reg).matcher(password).find()) {
            throw new ServiceException(ErrCode.UN_VALID_PWD);
        }
        // 姓名为 2 到 10 位
        if (stuName.length() < 2 || stuName.length() > 10) {
            throw new ServiceException(ErrCode.UN_VALID_STU_NAME_LEN);
        }
        // 姓名唯一
        LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
        qw.eq(Student::getStuName, stuName);
        Long cnt = studentMapper.selectCount(qw);
        if (cnt > 0) {
            throw new ServiceException(ErrCode.STU_NAME_EXIST);
        }
        // 密码加密
        student.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        // 保存
        int row = studentMapper.insert(student);
        if (row < 1) {
            throw new ServiceException(ErrCode.SERVER_ERROR);
        }
    }

    @Override
    public StudentLoginRes login(Student student, HttpSession session) {
        if (student == null) {
            throw new ServiceException(ErrCode.UN_VALID_PARAMS);
        }
        String stuNum = student.getStuNum();
        String password = student.getPassword();
        // 非空校验
        if (!StrUtil.isAllNotEmpty(stuNum, password)) {
            throw new ServiceException(ErrCode.UN_VALID_PARAMS);
        }
        // 密码加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 查询数据库是否有匹配
        LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
        qw.eq(Student::getStuNum, stuNum);
        qw.eq(Student::getPassword, password);
        qw.select(Student::getId);
        Student res = studentMapper.selectOne(qw);
        if (res == null || res.getId() == null) {
            throw new ServiceException(ErrCode.ERROR_UNAME_OR_PWD);
        }
        // 登录成功，生成 token，保存 session
        session.setAttribute("userId", res.getId());
        String token = JwtUtils.generateToken(res.getId(), jwtPrivateKey);
        return new StudentLoginRes(res.getId(), token);
    }

    @Override
    public void modifyInfo(Student student) {
        if (student == null) {
            throw new ServiceException(ErrCode.UN_VALID_PARAMS);
        }
        Long userId = student.getId();
        String stuName = student.getStuName();
        Integer pets3Grade = student.getPets3Grade();
        Integer pets4Grade = student.getPets4Grade();
        // 非空校验
        if (userId == null) {
            throw new ServiceException(ErrCode.UN_VALID_PARAMS);
        }
        // 查询原本信息
        Student cur = findById(userId);
        if (cur == null) {
            throw new ServiceException(ErrCode.USER_NOT_FOUND);
        }
        LambdaUpdateWrapper<Student> uw = new LambdaUpdateWrapper<>();
        uw.eq(Student::getId, userId);
        // 姓名必须唯一，2 到 10 位
        if (stuName != null && !stuName.equals(cur.getStuName())) {
            if (stuName.length() < 2 || stuName.length() > 10) {
                throw new ServiceException(ErrCode.UN_VALID_STU_NAME_LEN);
            }
            LambdaQueryWrapper<Student> qw = new LambdaQueryWrapper<>();
            qw.eq(Student::getStuName, stuName);
            if (studentMapper.selectCount(qw) > 0) {
                throw new ServiceException(ErrCode.STU_NAME_EXIST);
            }
            uw.set(Student::getStuName, stuName);
        }
        // 成绩不能为负数
        if (pets3Grade != null && pets3Grade >= 0) {
            uw.set(Student::getPets3Grade, pets3Grade);
        }
        if (pets4Grade != null && pets4Grade >= 0) {
            uw.set(Student::getPets4Grade, pets4Grade);
        }
        // 保存
        int row = studentMapper.update(null, uw);
        if (row < 1) {
            throw new ServiceException(ErrCode.SERVER_ERROR);
        }
    }
}
