package com.wyf.stu.mapper;

import com.wyf.stu.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangyifan
 * @since 2022-10-21
 */
public interface StudentMapper extends BaseMapper<Student> {
    @Update("update sys_student set password =#{newPassword} where name =#{name} and password=#{password}")
    int updatePassword(Student student);

}
