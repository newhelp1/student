package com.wyf.stu.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.Result;
import com.wyf.stu.config.interceptor.AuthAccess;
import com.wyf.stu.dto.AdminDto;
import com.wyf.stu.dto.AdminPasswordDto;
import com.wyf.stu.entity.Role;
import com.wyf.stu.entity.Validation;
import com.wyf.stu.exception.ServiceException;
import com.wyf.stu.service.IRoleService;
import com.wyf.stu.service.IValidationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wyf.stu.service.IAdminService;
import com.wyf.stu.entity.Admin;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-14
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private IAdminService adminService;

    @Resource
    private IValidationService validationService;

    @Resource
    private IRoleService roleService;


    //密码登录
    @AuthAccess
    @PostMapping("/login")
    public Result login(@RequestBody AdminDto adminDto) {
        AdminDto dto = adminService.login(adminDto);
        return Result.success(dto);
    }

    //邮箱登录
    @AuthAccess
    @PostMapping("/loginEmail")
    public Result loginEmail(@RequestBody AdminDto adminDto) {
        AdminDto dto = adminService.loginEamil(adminDto);
        if (!dto.isStatus()){
            return Result.error(Constants.CODE_400, "账户被禁用，请联系管理员");
        }
        return Result.success(dto);
    }

    //修改密码
    @AuthAccess
    @PostMapping("/password")
    public Result password(@RequestBody AdminPasswordDto adminPasswordDto) {
        adminService.updatePassword(adminPasswordDto);
        return Result.success();
    }

    //重置密码按钮
    @AuthAccess
    @PutMapping("/reset")
    public Result reset(@RequestBody AdminPasswordDto adminPasswordDto) {
        if (StrUtil.isBlank(adminPasswordDto.getEmail()) || StrUtil.isBlank(adminPasswordDto.getCode())) {
            throw new ServiceException("-1", "参数异常");
        }
        //先查询邮箱验证的表 看看之前有没有发送过 邮箱code 如果不存在就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", adminPasswordDto.getEmail());
        validationQueryWrapper.eq("code", adminPasswordDto.getCode());
        validationQueryWrapper.ge("time", new Date());//查询没过期的code ,where time >= new Date() 没过期
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException("-1", "验证码输入错误或已过期,请再次确认");
        }
        //查询如果验证通过就查询用户的信息
        QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("email", adminPasswordDto.getEmail()); //根据邮箱查询到用户信息
        Admin admin = adminService.getOne(adminQueryWrapper);
        if (admin==null){
            return Result.error(Constants.CODE_400,"该用户不存在，请先注册或先联系管理员");
        }

        //重置密码
        admin.setPassword("123456");
        adminService.updateById(admin);
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
        adminService.sendEmailCode(email, type);
        return Result.success();
    }

    //新增和修改
    @PostMapping
    public Result save(@RequestBody Admin admin) {
        if (admin.getPassword() == null) {
            admin.setPassword("123456");

            List<Role> roles = roleService.list();
            for (Role role : roles) {
                if (role.getFlag().equals("ROLE_ADMIN")){
                    admin.setRole(role.getFlag());
                }
            }

        }
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getNo, admin.getNo());
        int count1 = adminService.count(queryWrapper);
        if (count1>0) {
            return Result.error(Constants.CODE_400,"编号已存在");
        }
        LambdaQueryWrapper<Admin> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Admin::getName, admin.getName());
        int count2 = adminService.count(queryWrapper1);
        if (count2>0) {
            return Result.error(Constants.CODE_400,"账号已存在");
        }
        admin.setStatus(true);
        return Result.success(adminService.save(admin));
    }
    @PutMapping
    public Result update(@RequestBody Admin admin){
        return Result.success(adminService.updateById(admin));
    }



    //根据id删除
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(adminService.removeById(id));
    }

    //批量删除
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(adminService.removeByIds(ids));
    }

    //    查询所有
    @GetMapping
    public Result findAll() {
        return Result.success(adminService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(adminService.getById(id));
    }

    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String no,
                           @RequestParam String address,
                           @RequestParam String sex) {
        //构造分页构造器
        Page pageInfo = new Page(pageNum, pageSize);
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(no), Admin::getNo, no);
        queryWrapper.like(StringUtils.isNotEmpty(address), Admin::getAddress, address);
        queryWrapper.like(StringUtils.isNotEmpty(sex), Admin::getSex, sex);
        queryWrapper.orderByDesc(Admin::getId);
        adminService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 导出接口
     *
     * @param response
     * @return
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        List<Admin> list = adminService.list();
        //在内存中操作 写到浏览器中
        ExcelWriter writer = ExcelUtil.getWriter(true);
//        //自定义标题别名
//        writer.addHeaderAlias("no", "编号");
//        writer.addHeaderAlias("name", "账号");
//        writer.addHeaderAlias("password", "密码");
//        writer.addHeaderAlias("nickname", "姓名");
//        writer.addHeaderAlias("sex", "姓别");
//        writer.addHeaderAlias("email", "邮箱");
//        writer.addHeaderAlias("phone", "手机号");
//        writer.addHeaderAlias("address", "地址");
//        writer.addHeaderAlias("createTime", "创建时间");

        //一次性写出list内的对象到excel
        writer.write(list, true);
        //设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    @PostMapping("/import")
    public boolean imp(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<Admin> list = reader.readAll(Admin.class);

        adminService.saveBatch(list);
        return true;
    }

    @GetMapping("/name/{name}")
    public Result findOne(@PathVariable String name) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
////获取当前用户信息
//        Admin currentAdmin = TokenUtils.getCurrentAdmin();
//        System.out.println("当前登录用户:" + currentAdmin.getNickname());

        return Result.success(adminService.getOne(queryWrapper));
    }
}
