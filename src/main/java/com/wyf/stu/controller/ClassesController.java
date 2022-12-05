package com.wyf.stu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.Result;
import com.wyf.stu.entity.Select;
import com.wyf.stu.service.ISelectService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import com.wyf.stu.service.IClassesService;
import com.wyf.stu.entity.Classes;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器            班级管理
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-17
 */
@RestController
@RequestMapping("/classes")
public class ClassesController {

    @Resource
    private IClassesService classesService;

    @Resource
    private ISelectService selectService;


    //新增和修改
    @PostMapping
    public Result save(@RequestBody Classes classes) {
        return Result.success(classesService.saveOrUpdate(classes));
    }

    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Select::getClassesId,id);
        int count = selectService.count(queryWrapper);
        if (count>0){
            return Result.error(Constants.CODE_400,"该班级有关联的专业课，请先操作专业的课程");
        }
        return Result.success(classesService.removeById(id));
    }

    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        List<Integer> list = new ArrayList<>();
        for (Integer id : ids) {
            list.add(id);
            LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Select::getClassesId,id);
            int count = selectService.count(queryWrapper);
            if (count>0){
                return Result.error(Constants.CODE_400,"该班级有关联的专业课，请先操作专业的课程");
            }
        }
        return Result.success(classesService.removeByIds(ids));
    }

    //    查询所有
    @GetMapping
    public Result findAll() {
        return Result.success(classesService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(classesService.getById(id));
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String no,
                           @RequestParam String name) {
        Page<Classes> pageInfo = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Classes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(no!=null,Classes::getNo,no);
        queryWrapper.like(name!=null,Classes::getName,name);
        Page<Classes> page = classesService.page(pageInfo, queryWrapper);
        return Result.success(page);
    }
}
