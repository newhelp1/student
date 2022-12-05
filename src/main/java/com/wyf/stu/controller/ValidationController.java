package com.wyf.stu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.wyf.stu.commons.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.wyf.stu.service.IValidationService;
import com.wyf.stu.entity.Validation;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-15
 */
@RestController
@RequestMapping("/validation")
public class ValidationController {

    @Resource
    private IValidationService validationService;


    //新增和修改
    @PostMapping
    public Result save(@RequestBody Validation validation) {
        return Result.success(validationService.saveOrUpdate(validation));
    }

    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(validationService.removeById(id));
    }

    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(validationService.removeByIds(ids));
    }

    //    查询所有
    @GetMapping
    public Result findAll() {
        return Result.success(validationService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(validationService.getById(id));
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(validationService.page(new Page<>(pageNum, pageSize)));
    }
}
