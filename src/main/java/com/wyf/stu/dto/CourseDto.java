package com.wyf.stu.dto;

import com.wyf.stu.entity.Course;
import lombok.Data;

@Data
public class CourseDto extends Course {
    //专业名称
    private String majorName;
    //学院名称
    private String collegeName;

    //教师名称
    private String teacherName;
}
