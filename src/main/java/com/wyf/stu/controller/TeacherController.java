package com.wyf.stu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.Result;
import com.wyf.stu.config.interceptor.AuthAccess;
import com.wyf.stu.entity.*;
import com.wyf.stu.exception.ServiceException;
import com.wyf.stu.service.ICourseService;
import com.wyf.stu.service.IRoleService;
import com.wyf.stu.service.IValidationService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wyf.stu.service.ITeacherService;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-16
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private ITeacherService teacherService;

    @Resource
    private ICourseService courseService;

    @Resource
    private IValidationService validationService;

    @Resource
    private IRoleService roleService;




    @GetMapping("/total")
    public Result total(){
        Integer total = teacherService.total();
        return Result.success(total);
    }
    @GetMapping("/name/{name}")
    public Result findOne(@PathVariable String name) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
////获取当前用户信息
//        Admin currentAdmin = TokenUtils.getCurrentAdmin();
//        System.out.println("当前登录用户:" + currentAdmin.getNickname());

        return Result.success(teacherService.getOne(queryWrapper));
    }
    //密码登录
    @AuthAccess
    @PostMapping("/login")
    public Result login(@RequestBody Teacher teacher) {
        Teacher teacherLogin = teacherService.login(teacher);
        return Result.success(teacherLogin);
    }

    //邮箱登录
    @AuthAccess
    @PostMapping("/loginEmail")
    public Result loginEmail(@RequestBody Teacher teacher) {
       Teacher teacherEmail = teacherService.loginEmail(teacher);
        return Result.success(teacherEmail);
    }

    //修改密码
    @AuthAccess
    @PostMapping("/password")
    public Result password(@RequestBody Teacher teacher) {
        teacherService.updatePassword(teacher);
        return Result.success();
    }

//    //重置密码按钮
    @AuthAccess
    @PutMapping("/reset")
    public Result reset(@RequestBody Teacher teacher) {
        if (StrUtil.isBlank(teacher.getEmail()) || StrUtil.isBlank(teacher.getCode())) {
            throw new ServiceException("-1", "参数异常");
        }
        //先查询邮箱验证的表 看看之前有没有发送过 邮箱code 如果不存在就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", teacher.getEmail());
        validationQueryWrapper.eq("code", teacher.getCode());
        validationQueryWrapper.ge("time", new Date());//查询没过期的code ,where time >= new Date() 没过期
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException("-1", "验证码输入错误或已过期,请再次确认");
        }
        //查询如果验证通过就查询用户的信息
        QueryWrapper<Teacher> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("email", teacher.getEmail()); //根据邮箱查询到用户信息
        Teacher teacherReset= teacherService.getOne(adminQueryWrapper);
        if (teacherReset==null){
            return Result.error(Constants.CODE_400,"该用户不存在，请先注册或先联系管理员");
        }
        //重置密码
        teacherReset.setPassword("123456");
        teacherService.updateById(teacherReset);
        return Result.success();
    }

    //获取邮箱验证码
    @GetMapping("/email/{email}/{type}")
    public Result sendEmailCode(@PathVariable String email, @PathVariable Integer type) {
        if (StrUtil.isBlank(email)) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        if (type == null) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        teacherService.sendEmailCode(email, type);
        return Result.success();
    }



    //新增和修改
    @PostMapping
    public Result save(@RequestBody Teacher teacher) {
        if(teacher.getPassword()==null){
            teacher.setPassword("123456");

        }
        if (teacher.getId()==null){
            teacher.setStatus(true);
            List<Role> roles = roleService.list();
            for (Role role : roles) {
                if (role.getFlag().equals("ROLE_TEACHER")){
                    teacher.setRole(role.getFlag());
                }
            }
        }
        return Result.success(teacherService.saveOrUpdate(teacher));
    }
    @PutMapping
    public Result update(@RequestBody Teacher teacher){
        return Result.success(teacherService.updateById(teacher));
    }


    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getTeacherId,id);
        int count = courseService.count(queryWrapper);
        if (count>0){
            return Result.error(Constants.CODE_400,"该教师关联课程,请先修改课程授课教师");
        }
        return Result.success(teacherService.removeById(id));
    }

    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        List<Integer> list = new ArrayList<>();
        for (Integer id : ids) {
            list.add(id);
            LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Course::getTeacherId,id);
            int count = courseService.count(queryWrapper);
            if (count>0){
                return Result.error(Constants.CODE_400,"该教师关联课程,请先修改课程授课教师");
            }
        }
        return Result.success(teacherService.removeByIds(ids));
    }

    //    查询所有
    @GetMapping
    public Result findAll() {
        return Result.success(teacherService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(teacherService.getById(id));
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String nickname,
                           @RequestParam String sex) {
        Page<Teacher> pageInfo = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(nickname!=null,Teacher::getNickname,nickname);
        queryWrapper.like(sex!=null,Teacher::getSex,sex);
        queryWrapper.orderByDesc(Teacher::getNo);
        Page<Teacher> page = teacherService.page(pageInfo, queryWrapper);

        return Result.success(page);
    }
}
