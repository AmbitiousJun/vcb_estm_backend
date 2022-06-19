package com.ambitious.vcbestm.controller;

import com.ambitious.vcbestm.common.CommonResult;
import com.ambitious.vcbestm.exception.ErrCode;
import com.ambitious.vcbestm.exception.ServiceException;
import com.ambitious.vcbestm.filter.LocalUser;
import com.ambitious.vcbestm.pojo.dto.StudentLoginDTO;
import com.ambitious.vcbestm.pojo.dto.StudentModifyDTO;
import com.ambitious.vcbestm.pojo.dto.StudentRegisterDTO;
import com.ambitious.vcbestm.pojo.po.EstimateHistory;
import com.ambitious.vcbestm.pojo.po.Student;
import com.ambitious.vcbestm.pojo.vo.StudentLoginRes;
import com.ambitious.vcbestm.service.EstimateHistoryService;
import com.ambitious.vcbestm.service.StudentService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
    @ApiResponses({
        @ApiResponse(code = 2000, message = "请求成功"),
        @ApiResponse(code = 4012, message = "非法请求"),
        @ApiResponse(code = 4002, message = "用户不存在")
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
    @ApiResponses({
        @ApiResponse(code = 2000, message = "请求成功"),
        @ApiResponse(code = 4012, message = "非法请求")
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
    @ApiResponses({
        @ApiResponse(code = 2000, message = "请求成功"),
        @ApiResponse(code = 4001, message = "参数不足"),
        @ApiResponse(code = 4002, message = "用户不存在"),
        @ApiResponse(code = 4002, message = "用户不存在"),
        @ApiResponse(code = 4013, message = "姓名长度在 2 到 10 之间"),
        @ApiResponse(code = 4008, message = "姓名已存在"),
        @ApiResponse(code = 5000, message = "服务器异常"),
    })
    @PutMapping("/modify")
    public CommonResult<String> modifyInfo(@RequestBody StudentModifyDTO dto) {
        Student student = new Student();
        student.setId(dto.getUserId());
        student.setStuName(dto.getStuName());
        student.setPets3Grade(dto.getPets3Grade());
        student.setPets4Grade(dto.getPets4Grade());
        studentService.modifyInfo(student);
        return CommonResult.ok("修改成功");
    }

    @ApiOperation("注销登录")
    @ApiResponses({
        @ApiResponse(code = 2000, message = "请求成功")
    })
    @PostMapping("/logout")
    public CommonResult<String> logout(@ApiIgnore HttpSession session) {
        session.invalidate();
        return CommonResult.ok("注销成功");
    }

    @ApiOperation("（匿名）检查用户当前是否处于登录状态")
    @ApiResponses({
        @ApiResponse(code = 2000, message = "请求成功")
    })
    @GetMapping("/login/check")
    public CommonResult<?> checkLogin(@ApiIgnore HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return CommonResult.ok("未登录");
        }
        return CommonResult.ok("已登录");
    }

    @ApiOperation("（匿名）用户注册")
    @ApiResponses({
        @ApiResponse(code = 2000, message = "请求成功"),
        @ApiResponse(code = 4001, message = "参数不足"),
        @ApiResponse(code = 4004, message = "学号长度在 4 到 20 之间"),
        @ApiResponse(code = 4005, message = "学号不能包含特殊字符"),
        @ApiResponse(code = 4006, message = "密码长度在 8 到 20 之间"),
        @ApiResponse(code = 4007, message = "密码不能包含特殊字符"),
        @ApiResponse(code = 4013, message = "姓名长度在 2 到 10 之间"),
        @ApiResponse(code = 4008, message = "姓名已存在"),
        @ApiResponse(code = 5000, message = "服务器异常"),
    })
    @PostMapping("/register")
    public CommonResult<String> register(@RequestBody StudentRegisterDTO dto) {
        Student student = new Student();
        student.setStuNum(dto.getStuNum());
        student.setStuName(dto.getStuName());
        student.setPassword(dto.getPassword());
        studentService.register(student);
        return CommonResult.ok("注册成功");
    }

    @ApiOperation("（匿名）用户登录")
    @ApiResponses({
        @ApiResponse(code = 2000, message = "请求成功"),
        @ApiResponse(code = 4001, message = "参数不足"),
        @ApiResponse(code = 4009, message = "用户名或密码错误"),
    })
    @PostMapping("/login")
    public CommonResult<StudentLoginRes> login(@RequestBody StudentLoginDTO dto,
                                               @ApiIgnore HttpSession session) {
        Student student = new Student();
        student.setStuNum(dto.getStuNum());
        student.setPassword(dto.getPassword());
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
