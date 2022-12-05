package com.wyf.stu.service;

import com.wyf.stu.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangyifan
 * @since 2022-10-21
 */
public interface IStudentService extends IService<Student> {

    Student login(Student student);

    Student loginEmail(Student student);

    void sendEmailCode(String email, Integer type);

    void updatePassword(Student student);

}
