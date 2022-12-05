package com.wyf.stu.service;

import com.wyf.stu.dto.AdminDto;
import com.wyf.stu.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-16
 */
public interface ITeacherService extends IService<Teacher> {

    Integer total();


    Teacher login(Teacher teacher);

    Teacher loginEmail(Teacher teacher);

    void sendEmailCode(String email, Integer type);

    void updatePassword(Teacher teacher);

}
