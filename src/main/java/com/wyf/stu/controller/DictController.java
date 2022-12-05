package com.wyf.stu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;

import com.wyf.stu.commons.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.wyf.stu.service.IDictService;
import com.wyf.stu.entity.Dict;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangyifan
 * @since 2022-10-30
 */
@RestController
@RequestMapping("/dict")
public class DictController {

    @Resource
    private IDictService dictService;


    //新增和修改
    @PostMapping
    public Result save(@RequestBody Dict dict) {
        return Result.success(dictService.saveOrUpdate(dict));
    }

    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(dictService.removeById(id));
    }

    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(dictService.removeByIds(ids));
    }

    //    查询所有
    @GetMapping
    public Result findAll() {
        return Result.success(dictService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(dictService.getById(id));
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(dictService.page(new Page<>(pageNum, pageSize)));
    }
}
