package com.wyf.stu.service;

import com.wyf.stu.dto.CourseDto;
import com.wyf.stu.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-16
 */
public interface ICourseService extends IService<Course> {


    Integer total();

}
