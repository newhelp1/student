package com.wyf.stu.service.impl;

import com.wyf.stu.dto.CourseDto;
import com.wyf.stu.entity.Course;
import com.wyf.stu.mapper.CourseMapper;
import com.wyf.stu.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-16
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Resource
    private CourseMapper courseMapper;
    @Override
    public Integer total() {
        Integer integer = courseMapper.selectCount(null);
        return integer;
    }
}
