package com.wyf.stu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.Result;
import com.wyf.stu.mapper.CollegeMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.wyf.stu.service.ICollegeService;
import com.wyf.stu.entity.College;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器     学院管理
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-15
 */
@RestController
@RequestMapping("/college")
public class CollegeController {

    @Resource
    private ICollegeService collegeService;

    //新增和修改
    @PostMapping
    public Result save(@RequestBody College college) {
        return Result.success(collegeService.saveOrUpdate(college));
    }

    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        College college = collegeService.getById(id);
        Integer pid = college.getPid();
        if (pid==null){
            LambdaQueryWrapper<College> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(College::getPid,id);
            int count = collegeService.count(queryWrapper);
            if (count>0){
                return Result.error(Constants.CODE_400, "该学院有关联专业，请先操作关联专业");
            }
        }

        return Result.success(collegeService.removeById(id));
    }

    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        List<Integer> list = new ArrayList<>();
        for (Integer id : ids) {
            list.add(id);
            College college = collegeService.getById(id);
            Integer pid = college.getPid();
            if (pid==null){
                LambdaQueryWrapper<College> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(College::getPid,id);
                int count = collegeService.count(queryWrapper);
                if (count>0){
                    return Result.error(Constants.CODE_400, "该学院有关联专业，请先操作关联专业");
                }
            }
        }
        return Result.success(collegeService.removeByIds(ids));
    }


    //    查询所有
    @GetMapping("/findall")
    public Result findAll() {
        return Result.success(collegeService.list());
    }

    //查询所有父级学院
    @GetMapping("/parent")
    public Result parent() {
        //查询所有数据
        List<College> list = collegeService.list();
        //找出pid为null的一级菜单
        List<College> parentNode = list.stream()
                .filter(college -> college.getPid() == null).collect(Collectors.toList());
        return Result.success(parentNode);
    }
    //查询所有父级学院
    @GetMapping("/children")
    public Result children() {
        //查询所有数据
        List<College> list = collegeService.list();
        //找出pid为null的一级菜单
        List<College> parentNode = list.stream()
                .filter(college -> college.getPid() != null).collect(Collectors.toList());
        return Result.success(parentNode);
    }


    //    查询所有
    @GetMapping
    public Result findAll(@RequestParam Integer code,
                          @RequestParam String name) {
        LambdaQueryWrapper<College> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(code != null, College::getCode, code);
        queryWrapper.like(name != null, College::getName, name);
        queryWrapper.orderByAsc(College::getCode);
        //查询所有数据
        List<College> list = collegeService.list(queryWrapper);
        //找出pid为null的一级菜单
        List<College> parentNode = list.stream()
                .filter(college -> college.getPid() == null).collect(Collectors.toList());
        //找出一级菜单的子菜单
        for (College college : parentNode) {
            college.setChildren(list.stream().filter(c -> college.getId().equals(c.getPid())).collect(Collectors.toList()));
        }
        return Result.success(parentNode);
    }


    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(collegeService.getById(id));
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam Integer code,
                           @RequestParam String name) {
        Page<College> pageInfo = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<College> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(code != null, College::getCode, code);
        queryWrapper.like(name != null, College::getName, name);
        queryWrapper.orderByAsc(College::getCode);
        Page<College> page = collegeService.page(pageInfo, queryWrapper);

        return Result.success(page);
    }
}
