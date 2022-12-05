package com.wyf.stu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.Result;
import com.wyf.stu.commons.ValidationEnum;
import com.wyf.stu.dto.AdminDto;
import com.wyf.stu.dto.AdminPasswordDto;
import com.wyf.stu.entity.Admin;
import com.wyf.stu.entity.Menu;
import com.wyf.stu.entity.RoleMenu;
import com.wyf.stu.entity.Validation;
import com.wyf.stu.exception.ServiceException;
import com.wyf.stu.mapper.AdminMapper;
import com.wyf.stu.mapper.RoleMapper;
import com.wyf.stu.mapper.RoleMenuMapper;
import com.wyf.stu.service.IAdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyf.stu.service.IMenuService;
import com.wyf.stu.service.IValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-14
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Resource
    private AdminMapper adminMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    JavaMailSender javaMailSender;
    @Resource
    private IValidationService validationService;

    @Resource
    private IMenuService menuService;

    @Value("${spring.mail.username}")
    private String from;

    private static final Log LOG = Log.get();
    @Override
    public AdminDto login(AdminDto adminDto) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", adminDto.getName());
        queryWrapper.eq("password", adminDto.getPassword());
        Admin one;
        try {
            one = getOne(queryWrapper);//从数据查询信息

        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统忙");
        }
        if (one != null) {
            if (!one.isStatus()){
                throw  new ServiceException("-1","账户被禁用，请联系管理员");
            }
            BeanUtil.copyProperties(one,adminDto,true); //copy属性

            String role = one.getRole(); //role_admin
            //设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(role);

            adminDto.setMenus(roleMenus);
            return adminDto;
        } else {
            throw new ServiceException("-1", "用户名或密码输入错误");
        }
    }

    /**
     * 获取当前角色的菜单列表
     * @param roleFlag
     * @return
     */
    public List<Menu> getRoleMenus(String roleFlag){
        Integer roleId = roleMapper.selectByFlag(roleFlag);

        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        //查出所有菜单
        List<Menu> menus = menuService.findMenus("");
        //new 一个最后筛选完之后的list
        List<Menu> roleMenus = new ArrayList<>();
        //筛选当前用户的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())){
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();

            children.removeIf(child ->!menuIds.contains(child.getId()));
        }
        return roleMenus;
    }


    @Override
    public AdminDto loginEamil(AdminDto adminDto) {

        String email = adminDto.getEmail();
        String code = adminDto.getCode();

        //先查询邮箱验证的表 看看之前有没有发送过 邮箱code 如果不存在就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email",email);
        validationQueryWrapper.eq("code",code);
        validationQueryWrapper.ge("time",new Date());//查询没过期的code ,where time >= new Date() 没过期
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null){
            throw  new ServiceException("-1","验证码输入错误或已过期,请再次确认");
        }
        //查询如果验证通过就查询用户的信息
        QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
        adminQueryWrapper.eq("email", email); //根据邮箱查询到用户信息
        Admin admin = getOne(adminQueryWrapper);

        if (admin == null){
            throw  new ServiceException("-1","未找到用户,请联系管理员");
        }
        if (!admin.isStatus()){
            throw  new ServiceException("-1","账户被禁用，请联系管理员");
        }

        BeanUtil.copyProperties(admin,adminDto,true);
//        //设置token
//        String token = TokenUtils.genToken(admin.getId().toString(), admin.getAdminpwd());
//        adminDto.setToken(token);
        return adminDto;
    }

    @Override
    public void updatePassword(AdminPasswordDto adminPasswordDto) {
        int update = adminMapper.updatePassword(adminPasswordDto);
        if (update<1){
            throw new ServiceException(Constants.CODE_600,"密码错误");
        }
    }

    @Override
    public void sendEmailCode(String email, Integer type) {
        Date now = new Date();
        //先查询同类型的code
        QueryWrapper<Validation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        queryWrapper.eq("type", type);
        queryWrapper.gt("time", now);   //查询数据库没过期的code
        Validation validation = validationService.getOne(queryWrapper);
        if (validation!=null){
            throw  new ServiceException("-1","当前您的验证码仍然有效,请不要重复点击");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); //发送人
        message.setSentDate(now);
        message.setTo(email);

        String code = RandomUtil.randomNumbers(4); //随机获得一个4为长度的验证码
        System.out.println(code);
        if (ValidationEnum.LOGIN.getCode().equals(type)){
            message.setSubject("【王一帆问你要验证码】:登陆密码验证码");
            message.setText("您本次登录的验证码是:" + code + ",请妥善保管,切勿泄露,有效期五分钟。") ;
        }else if (ValidationEnum.FORGET_PASS.getCode().equals(type)){
            message.setSubject("【王一帆问你要验证码】:找回密码验证码");
            message.setText("您本次找回密码的验证码是:" + code + ",请妥善保管,切勿泄露,有效期五分钟。") ;
        }

//        message.setCc("抄送人");
//        message.setBcc("密送人");
        javaMailSender.send(message);

        //发送成功之后把验证码存到数据库

        validationService.saveCode(email,code, type, DateUtil.offsetMinute(now,5));

    }
}
