package com.wyf.stu.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.wyf.stu.commons.Result;
import com.wyf.stu.entity.Course;
import com.wyf.stu.entity.Student;
import com.wyf.stu.service.ICourseService;
import com.wyf.stu.service.IStudentService;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import com.wyf.stu.service.ISelectService;
import com.wyf.stu.entity.Select;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 * 课表
 *
 * @author wangyifan
 * @since 2022-09-18
 */
@RestController
@RequestMapping("/select")
public class SelectController {

    @Resource
    private ISelectService selectService;
    @Resource
    private ICourseService courseService;
    @Resource
    private IStudentService studentService;

    //新增和修改
    @PostMapping("/teacher/{majorId}")
    public Result save(@PathVariable Integer majorId, @RequestBody List<Select> select) {
        selectService.saveAll(majorId, select);
        return Result.success();
    }

    //新增和修改
    @PostMapping("/student/{majorId}")
    public Result studentSave(@PathVariable Integer majorId,@RequestParam Integer id, @RequestBody List<Select> select) {
        selectService.saveStudentAll(majorId,id, select);
        return Result.success();
    }

    @GetMapping("/courseType/{id}")
    public Result selectCourse(@PathVariable Integer id) {
        Student student = studentService.getById(id);

        LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Select::getStudentId, id);
        queryWrapper.eq(Select::getMajorId, student.getMajorId());
        queryWrapper.eq(Select::getGradeId, student.getGradeId());
        queryWrapper.eq(Select::getClassesId, student.getClassesId());
        List<Select> lists = selectService.list(queryWrapper);
        List<Select> selectList = new ArrayList<>();
        for (Select list : lists) {
            LambdaQueryWrapper<Course> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Course::getId, list.getCourseId());
            Course course = courseService.getOne(queryWrapper1);
            if (course.getType().equals("否")) {
                selectList.add(list);
            }
        }
        return Result.success(selectList);
    }


    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(selectService.removeById(id));
    }

    // 查询课表
    @GetMapping("/major/{majorId}")
    public Result findByMajor(@PathVariable Integer majorId) {

        List<Select> selectList = selectService.list(new QueryWrapper<Select>().eq("major_id", majorId));
        ArrayList<Select> list = new ArrayList<>();
        for (Select select : selectList) {
            if (select.getStudentId()==null){
                list.add(select);
            }
        }
        return Result.success(list);
    }


    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(selectService.removeByIds(ids));
    }

    //    查询所有
    @GetMapping
    public Result findAll() {
        return Result.success(selectService.list());
    }

    //    查询课程
    @GetMapping("/courseTable")
    public Result findCourseTable(@RequestParam Integer studentId,
                                  @RequestParam Integer majorId,
                                  @RequestParam Integer gradeId,
                                  @RequestParam Integer classesId) {
        ArrayList<JSONObject> list = CollUtil.newArrayList();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jsonObject3 = new JSONObject();
        JSONObject jsonObject4 = new JSONObject();
        JSONObject jsonObject5 = new JSONObject();
        jsonObject1.set("section", JSONUtil.parseObj("{\"num\":\"第一大节\",\"time\":\"08:00-09:40\"}"));
        jsonObject2.set("section", JSONUtil.parseObj("{\"num\":\"第二大节\",\"time\":\"09:50-11:30\"}"));
        jsonObject3.set("section", JSONUtil.parseObj("{\"num\":\"第三大节\",\"time\":\"14:00-15:40\"}"));
        jsonObject4.set("section", JSONUtil.parseObj("{\"num\":\"第四大节\",\"time\":\"15:50-17:30\"}"));
        jsonObject5.set("section", JSONUtil.parseObj("{\"num\":\"第五大节\",\"time\":\"19:00-20:30\"}"));
        list.add(jsonObject1);
        list.add(jsonObject2);
        list.add(jsonObject3);
        list.add(jsonObject4);
        list.add(jsonObject5);

        LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Select::getMajorId, majorId);
        queryWrapper.eq(Select::getGradeId, gradeId);
        queryWrapper.eq(Select::getClassesId, classesId);
        List<Select> selectList = selectService.list(queryWrapper);
        for (Select select : selectList) {
            if (select.getStudentId()==null || select.getStudentId().equals(studentId)) {
                    Integer courseId = select.getCourseId();
                    Course course = courseService.getById(courseId);
                    String weekDay = select.getWeekDay();
                    String section = select.getSection();
                    switch (section) {
                        case "第一大节":
                            setJson(weekDay, course, jsonObject1);
                            break;
                        case "第二大节":
                            setJson(weekDay, course, jsonObject2);
                            break;
                        case "第三大节":
                            setJson(weekDay, course, jsonObject3);
                            break;
                        case "第四大节":
                            setJson(weekDay, course, jsonObject4);
                            break;
                        case "第五大节":
                            setJson(weekDay, course, jsonObject5);
                            break;
                    }
            }
        }
        return Result.success(list);
    }

    //    查询课程
    @GetMapping("/AdminCourseTable")
    public Result findCourse(@RequestParam Integer majorId,
                             @RequestParam Integer gradeId,
                             @RequestParam Integer classesId) {
        ArrayList<JSONObject> list = CollUtil.newArrayList();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        JSONObject jsonObject3 = new JSONObject();
        JSONObject jsonObject4 = new JSONObject();
        JSONObject jsonObject5 = new JSONObject();
        jsonObject1.set("section", JSONUtil.parseObj("{\"num\":\"第一大节\",\"time\":\"08:00-09:40\"}"));
        jsonObject2.set("section", JSONUtil.parseObj("{\"num\":\"第二大节\",\"time\":\"09:50-11:30\"}"));
        jsonObject3.set("section", JSONUtil.parseObj("{\"num\":\"第三大节\",\"time\":\"14:00-15:40\"}"));
        jsonObject4.set("section", JSONUtil.parseObj("{\"num\":\"第四大节\",\"time\":\"15:50-17:30\"}"));
        jsonObject5.set("section", JSONUtil.parseObj("{\"num\":\"第五大节\",\"time\":\"19:00-20:30\"}"));
        list.add(jsonObject1);
        list.add(jsonObject2);
        list.add(jsonObject3);
        list.add(jsonObject4);
        list.add(jsonObject5);

        LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Select::getMajorId, majorId);
        queryWrapper.eq(Select::getGradeId, gradeId);
        queryWrapper.eq(Select::getClassesId, classesId);
        List<Select> selectList = selectService.list(queryWrapper);
        for (Select select : selectList) {
            if (select.getStudentId()==null){
                Integer courseId = select.getCourseId();
                Course course = courseService.getById(courseId);
                String weekDay = select.getWeekDay();
                String section = select.getSection();
                switch (section) {
                    case "第一大节":
                        setJson(weekDay, course, jsonObject1);
                        break;
                    case "第二大节":
                        setJson(weekDay, course, jsonObject2);
                        break;
                    case "第三大节":
                        setJson(weekDay, course, jsonObject3);
                        break;
                    case "第四大节":
                        setJson(weekDay, course, jsonObject4);
                        break;
                    case "第五大节":
                        setJson(weekDay, course, jsonObject5);
                        break;
                }
            }
        }

        return Result.success(list);
    }


    private JSONObject setJson(String weekDay, Course course, JSONObject jsonObject) {
        if ("周一".equals(weekDay)) {
            jsonObject.set("mon", course);
        } else if ("周二".equals(weekDay)) {
            jsonObject.set("tue", course);
        } else if ("周三".equals(weekDay)) {
            jsonObject.set("wes", course);
        } else if ("周四".equals(weekDay)) {
            jsonObject.set("thu", course);
        } else if ("周五".equals(weekDay)) {
            jsonObject.set("fri", course);
        } else if ("周六".equals(weekDay)) {
            jsonObject.set("sat", course);
        } else if ("周日".equals(weekDay)) {
            jsonObject.set("sun", course);
        }
        return jsonObject;
    }




    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(selectService.getById(id));
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(selectService.page(new Page<>(pageNum, pageSize)));
    }

    /**
     * 设置课程模糊查询
     *
     * @param majorId
     * @param gradeId
     * @param classesId
     * @return
     */
    @GetMapping("/course")
    public Result classes(@RequestParam Integer majorId,
                          @RequestParam Integer gradeId,
                          @RequestParam Integer classesId,
                          @RequestParam Integer courseId) {
        LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Select::getMajorId, majorId);
        queryWrapper.eq(gradeId != null, Select::getGradeId, gradeId);
        queryWrapper.eq(classesId != null, Select::getClassesId, classesId);
        queryWrapper.eq(courseId != null, Select::getCourseId, courseId);
        List<Select> list = selectService.list(queryWrapper);
        return Result.success(list);
    }
}
