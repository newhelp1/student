package com.wyf.stu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.Result;
import com.wyf.stu.config.interceptor.AuthAccess;
import com.wyf.stu.dto.CourseDto;
import com.wyf.stu.dto.StudentDto;
import com.wyf.stu.entity.*;
import com.wyf.stu.exception.ServiceException;
import com.wyf.stu.mapper.StudentMapper;
import com.wyf.stu.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangyifan
 * @since 2022-10-21
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private IStudentService studentService;

    @Resource
    private ICollegeService collegeService;

    @Resource
    private IGradeService gradeService;

    @Resource
    private IClassesService classesService;

    @Resource
    private IValidationService validationService;

    @Resource
    private IRoleService roleService;



    @GetMapping("/name/{name}")
    public Result findOne(@PathVariable String name) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        return Result.success(studentService.getOne(queryWrapper));
    }
    //密码登录
    @PostMapping("/login")
    public Result login(@RequestBody Student student) {
        Student studentLogin = studentService.login(student);
        return Result.success(studentLogin);
    }

    //邮箱登录
    @AuthAccess
    @PostMapping("/loginEmail")
    public Result loginEmail(@RequestBody Student student) {
        Student studentEmail = studentService.loginEmail(student);
        return Result.success(studentEmail);
    }
    //
//    //修改密码
    @AuthAccess
    @PostMapping("/password")
    public Result password(@RequestBody Student student) {
        studentService.updatePassword(student);
        return Result.success();
    }
//
//    //重置密码按钮
    @AuthAccess
    @PutMapping("/reset")
    public Result reset(@RequestBody Student student) {
        if (StrUtil.isBlank(student.getEmail()) || StrUtil.isBlank(student.getCode())) {
            throw new ServiceException("-1", "参数异常");
        }
        //先查询邮箱验证的表 看看之前有没有发送过 邮箱code 如果不存在就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", student.getEmail());
        validationQueryWrapper.eq("code", student.getCode());
        validationQueryWrapper.ge("time", new Date());//查询没过期的code ,where time >= new Date() 没过期
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException("-1", "验证码输入错误或已过期,请再次确认");
        }
        //查询如果验证通过就查询用户的信息
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", student.getEmail()); //根据邮箱查询到用户信息
        Student studentReset= studentService.getOne(queryWrapper);
        if (studentReset==null){
            return Result.error(Constants.CODE_400,"该用户不存在，请先注册或先联系管理员");
        }
        //重置密码
        studentReset.setPassword("123456");
        studentService.updateById(studentReset);
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
        studentService.sendEmailCode(email, type);
        return Result.success();
    }




    //新增和修改
    @PostMapping
    public Result save(@RequestBody Student student) {
        LambdaQueryWrapper<Student> queryWrapper1 = new LambdaQueryWrapper<>();
        if (student.getId()==null){
            student.setStatus(true);
            student.setPassword("123456");
            queryWrapper1.eq(Student::getSno,student.getSno());
            int count = studentService.count(queryWrapper1);
            if (count>0){
                return Result.error(Constants.CODE_400,"学号已存在");
            }
            List<Role> roles = roleService.list();
            for (Role role : roles) {
                if (role.getFlag().equals("ROLE_STUDENT")){
                    student.setRole(role.getFlag());
                }
            }
        }

        Integer collegeId = student.getCollegeId();
        LambdaQueryWrapper<College> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(College::getPid,collegeId);
        List<College> collegeList = collegeService.list(queryWrapper);
        for (College college : collegeList) {
            ArrayList<College> collegeArrayList = new ArrayList<>();
            collegeArrayList.add(college);
            if (student.getMajorId().equals(college.getId())){
                return Result.success(studentService.saveOrUpdate(student));
            }
        }
        return Result.error(Constants.CODE_400,"该学院没有此专业，请重新选择");
    }
    @PutMapping
    public Result update(@RequestBody Student student){
        return Result.success(studentService.updateById(student));
    }




    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(studentService.removeById(id));
    }

    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(studentService.removeByIds(ids));
    }

    //    查询所有
    @GetMapping
    public Result findAll() {
        return Result.success(studentService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(studentService.getById(id));
    }


    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam Integer collegeId,
                           @RequestParam Integer majorId,
                           @RequestParam Integer gradeId,
                           @RequestParam Integer classesId,
                           @RequestParam String nickname
                           ) {
        Page<Student> pageInfo = new Page<>(pageNum, pageSize);
        Page<StudentDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(collegeId!=null,Student::getCollegeId,collegeId);
        queryWrapper.eq(majorId!=null,Student::getMajorId,majorId);
        queryWrapper.eq(gradeId!=null,Student::getGradeId,gradeId);
        queryWrapper.eq(classesId!=null,Student::getClassesId,classesId);
        queryWrapper.like(nickname!=null,Student::getNickname,nickname);
        queryWrapper.orderByDesc(Student::getSno);
        studentService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Student> records = pageInfo.getRecords();

        List<StudentDto> list = records.stream().map((item) -> {
            StudentDto studentDto = new StudentDto();
            BeanUtils.copyProperties(item, studentDto);

            Integer collegeId1 = item.getCollegeId();
            College college = collegeService.getById(collegeId1);
            if (college!=null && college.getPid()==null){
                    String collegeName = college.getName();
                    studentDto.setCollegeName(collegeName);
            }

            Integer majorId1 = item.getMajorId();
            College collegeKid = collegeService.getById(majorId1);
            if (collegeKid!=null &&collegeKid.getPid()!=null){
                String collegeKidName = collegeKid.getName();
                studentDto.setMajorName(collegeKidName);
            }

            Integer gradeId1 = item.getGradeId();
            Grade grade = gradeService.getById(gradeId1);
            if (grade!=null){
                String gradeName = grade.getName();
                studentDto.setGradeName(gradeName);
            }

            Integer classesId1 = item.getClassesId();
            Classes classes = classesService.getById(classesId1);
            if (classes!=null){
                String classesName = classes.getName();
                studentDto.setClassesName(classesName);
            }

            return studentDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        return Result.success(dtoPage);
    }
}
