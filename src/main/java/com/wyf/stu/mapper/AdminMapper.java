package com.wyf.stu.mapper;

import com.wyf.stu.dto.AdminPasswordDto;
import com.wyf.stu.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-14
 */
public interface AdminMapper extends BaseMapper<Admin> {

    @Update("update sys_admin set password =#{newPassword} where name =#{name} and password=#{password}")
    int updatePassword(AdminPasswordDto adminPasswordDto);
}
