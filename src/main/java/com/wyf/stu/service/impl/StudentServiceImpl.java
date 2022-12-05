package com.wyf.stu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.ValidationEnum;
import com.wyf.stu.entity.Menu;
import com.wyf.stu.entity.Student;
import com.wyf.stu.entity.Teacher;
import com.wyf.stu.entity.Validation;
import com.wyf.stu.exception.ServiceException;
import com.wyf.stu.mapper.StudentMapper;
import com.wyf.stu.mapper.TeacherMapper;
import com.wyf.stu.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyf.stu.service.IValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wangyifan
 * @since 2022-10-21
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {
    @Resource
    private StudentMapper studentMapper;

    @Resource
    private IValidationService validationService;

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    JavaMailSender javaMailSender;

    @Resource
    private AdminServiceImpl adminServiceImpl;

    @Override
    public Student login(Student student) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", student.getName());
        queryWrapper.eq("password", student.getPassword());
        Student studentOne = this.getOne(queryWrapper);
        if (studentOne != null) {
            if (!studentOne.isStatus()) {
                throw new ServiceException("-1", "账户被禁用，请联系管理员");
            }
            BeanUtil.copyProperties(studentOne, student, true); //copy属性
            String role = studentOne.getRole();
            List<Menu> roleMenus = adminServiceImpl.getRoleMenus(role);
            student.setMenus(roleMenus);
            return student;
        } else {
            throw new ServiceException("-1", "用户名或密码输入错误");
        }


    }

    @Override
    public Student loginEmail(Student student) {

        String email = student.getEmail();
        String code = student.getCode();

        //先查询邮箱验证的表 看看之前有没有发送过 邮箱code 如果不存在就重新获取
        QueryWrapper<Validation> validationQueryWrapper = new QueryWrapper<>();
        validationQueryWrapper.eq("email", email);
        validationQueryWrapper.eq("code", code);
        validationQueryWrapper.ge("time", new Date());//查询没过期的code ,where time >= new Date() 没过期
        Validation one = validationService.getOne(validationQueryWrapper);
        if (one == null) {
            throw new ServiceException("-1", "验证码输入错误或已过期,请再次确认");
        }
        //查询如果验证通过就查询用户的信息
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email); //根据邮箱查询到用户信息
        Student studentrEmail = getOne(queryWrapper);

        if (studentrEmail == null) {
            throw new ServiceException("-1", "未找到用户,请联系管理员");
        }
        if (!studentrEmail.isStatus()) {
            throw new ServiceException("-1", "账户被禁用，请联系管理员");
        }

        BeanUtil.copyProperties(studentrEmail, student, true);
//        //设置token
//        String token = TokenUtils.genToken(admin.getId().toString(), admin.getAdminpwd());
//        adminDto.setToken(token);
        return student;
    }

    @Override
    public void sendEmailCode(String email, Integer type) {

        Date now = new Date();
        //先查询同类型的code
        QueryWrapper<Validation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("type", type);
        queryWrapper.gt("time", now);   //查询数据库没过期的code
        Validation validation = validationService.getOne(queryWrapper);
        if (validation != null) {
            throw new ServiceException("-1", "当前您的验证码仍然有效,请不要重复点击");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from); //发送人
        message.setSentDate(now);
        message.setTo(email);

        String code = RandomUtil.randomNumbers(4); //随机获得一个4为长度的验证码
        System.out.println(code);
        if (ValidationEnum.LOGIN.getCode().equals(type)) {
            message.setSubject("【王一帆喊你】:登陆密码验证码");
            message.setText("您本次登录的验证码是:" + code + ",请妥善保管,切勿泄露,有效期五分钟。");
        } else if (ValidationEnum.FORGET_PASS.getCode().equals(type)) {
            message.setSubject("【王一帆喊你】:找回密码验证码");
            message.setText("您本次找回密码的验证码是:" + code + ",请妥善保管,切勿泄露,有效期五分钟。");
        }

//        message.setCc("抄送人");
//        message.setBcc("密送人");
        javaMailSender.send(message);
        //发送成功之后把验证码存到数据库
        validationService.saveCode(email, code, type, DateUtil.offsetMinute(now, 5));

    }

    @Override
    public void updatePassword(Student student) {
        int update = studentMapper.updatePassword(student);
        if (update<1){
            throw new ServiceException(Constants.CODE_600,"密码错误");
        }
    }

}

