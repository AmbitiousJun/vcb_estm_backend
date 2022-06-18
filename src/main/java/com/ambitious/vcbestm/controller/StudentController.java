package com.ambitious.vcbestm.controller;

import com.ambitious.vcbestm.common.CommonResult;
import com.ambitious.vcbestm.exception.ErrCode;
import com.ambitious.vcbestm.exception.ServiceException;
import com.ambitious.vcbestm.filter.LocalUser;
import com.ambitious.vcbestm.pojo.po.EstimateHistory;
import com.ambitious.vcbestm.pojo.po.Student;
import com.ambitious.vcbestm.pojo.vo.StudentLoginRes;
import com.ambitious.vcbestm.service.EstimateHistoryService;
import com.ambitious.vcbestm.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 用户业务控制器
 * @author Ambitious
 * @date 2022/6/17 17:18
 */
@Api(tags = "用户接口")
@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;
    @Resource
    private EstimateHistoryService estimateHistoryService;

    @ApiOperation("查询用户信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name="uid",value="用户id",required=true,paramType="path",dataType="Long")
    })
    @GetMapping("/{uid}")
    public CommonResult<Student> findById(@PathVariable Long uid) {
        if (!isLegalUser(uid)) {
            throw new ServiceException(ErrCode.ILLEGAL_REQUEST);
        }
        Student student = studentService.findById(uid);
        if (student == null) {
            throw new ServiceException(ErrCode.USER_NOT_FOUND);
        }
        return CommonResult.ok(student);
    }

    @ApiOperation("查询用户的词汇评估历史记录")
    @ApiImplicitParams({
        @ApiImplicitParam(name="uid",value="用户id",required=true,paramType="path",dataType="Long")
    })
    @GetMapping("/estimate/history/{uid}")
    public CommonResult<List<EstimateHistory>> estimateHistory(@PathVariable Long uid) {
        if (!isLegalUser(uid)) {
            throw new ServiceException(ErrCode.ILLEGAL_REQUEST);
        }
        List<EstimateHistory> list = estimateHistoryService.findListByUid(uid);
        return CommonResult.ok(list);
    }

    @ApiOperation("修改用户信息")
    @PutMapping("/modify")
    public CommonResult<String> modifyInfo(@RequestBody Student student) {
        studentService.modifyInfo(student);
        return CommonResult.ok("修改成功");
    }

    @ApiOperation("注销登录")
    @PostMapping("/logout")
    public CommonResult<String> logout(HttpSession session) {
        session.invalidate();
        return CommonResult.ok("注销成功");
    }

    @ApiOperation("（匿名）检查用户当前是否处于登录状态")
    @GetMapping("/login/check")
    public CommonResult<?> checkLogin(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return CommonResult.ok("未登录");
        }
        return CommonResult.ok("已登录");
    }

    @ApiOperation("（匿名）用户注册")
    @PostMapping("/register")
    public CommonResult<String> register(@RequestBody Student student) {
        studentService.register(student);
        return CommonResult.ok("注册成功");
    }

    @ApiOperation("（匿名）用户登录")
    @PostMapping("/login")
    public CommonResult<StudentLoginRes> login(@RequestBody Student student,
                                               HttpSession session) {
        StudentLoginRes res = studentService.login(student, session);
        return CommonResult.ok(res);
    }

    /**
     * 判断用户是否合法
     * @param userId userId
     * @return 合法 true
     */
    private boolean isLegalUser(Long userId) {
        Long curUserId = LocalUser.get();
        return curUserId != null && curUserId.equals(userId);
    }
}
