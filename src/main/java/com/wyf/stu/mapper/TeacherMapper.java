package com.wyf.stu.mapper;

import com.wyf.stu.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-16
 */
public interface TeacherMapper extends BaseMapper<Teacher> {

    @Update("update sys_teacher set password =#{newPassword} where name =#{name} and password=#{password}")
    int updatePassword(Teacher teacher);

}
