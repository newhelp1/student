package com.wyf.stu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.Result;
import com.wyf.stu.dto.CourseDto;
import com.wyf.stu.entity.*;
import com.wyf.stu.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器    课程管理
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-16
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private ICourseService courseService;

    @Resource
    private ISelectService selectService;

    @Resource
    private ITeacherService teacherService;


    @GetMapping("/total")
    public Result total(){
        Integer total = courseService.total();
        return Result.success(total);
    }

        //新增和修改
    @PostMapping
    public Result save(@RequestBody Course course) {
        return Result.success(courseService.saveOrUpdate(course));
    }


    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Select::getCourseId,id);
        int count = selectService.count(queryWrapper);
        if (count>0){
            return Result.error(Constants.CODE_400,"该课程有关联的专业课，请先操作专业的课程");
        }
        return Result.success(courseService.removeById(id));
    }

    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        List<Integer> list = new ArrayList<>();
        for (Integer id : ids) {
            list.add(id);
            LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Select::getCourseId,id);
            int count = selectService.count(queryWrapper);
            if (count>0){
                return Result.error(Constants.CODE_400,"该课程有关联的专业课，请先操作专业的课程");
            }
        }
        return Result.success(courseService.removeByIds(ids));
    }

    //    查询所有
    @GetMapping
    public Result findAll() {
        return Result.success(courseService.list());
    }

    //查询必修课
    @PostMapping("/findCompulsory")
    public Result findCompulsory(){
        List<Course> courses = courseService.list();
        ArrayList<Course> list = new ArrayList<>();

        for (Course course : courses) {
            if (course.getType().equals("是")){
                list.add(course);
            }
        }
        return Result.success(list);
    }


    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(courseService.getById(id));
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String no,
                           @RequestParam String name) {
        Page<Course> pageInfo = new Page<>(pageNum, pageSize);
        Page<CourseDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(no != null, Course::getNo, no);
        queryWrapper.like(name != null, Course::getName, name);
        queryWrapper.orderByDesc(Course::getNo);
        courseService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Course> records = pageInfo.getRecords();

        List<CourseDto> list = records.stream().map((item) -> {
            CourseDto courseDto = new CourseDto();
            BeanUtils.copyProperties(item, courseDto);
            Integer teacherId = item.getTeacherId();
            Teacher teacher = teacherService.getById(teacherId);
            if (teacher!=null){
                String teacherNickname = teacher.getNickname();
                courseDto.setTeacherName(teacherNickname);
            }
            return courseDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);

        return Result.success(dtoPage);
    }
}
