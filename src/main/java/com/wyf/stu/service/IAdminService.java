package com.wyf.stu.service;

import com.wyf.stu.commons.Result;
import com.wyf.stu.dto.AdminDto;
import com.wyf.stu.dto.AdminPasswordDto;
import com.wyf.stu.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-14
 */
public interface IAdminService extends IService<Admin> {

    AdminDto login(AdminDto adminDto);

    AdminDto loginEamil(AdminDto adminDto);

    void updatePassword(AdminPasswordDto adminPasswordDto);

    void sendEmailCode(String email, Integer type);
}
