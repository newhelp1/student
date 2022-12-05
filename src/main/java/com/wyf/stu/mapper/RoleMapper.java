package com.wyf.stu.mapper;

import com.wyf.stu.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangyifan
 * @since 2022-10-26
 */
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select id from sys_role where flag =#{flag}")
    Integer selectByFlag(@Param("flag") String flag);

}
