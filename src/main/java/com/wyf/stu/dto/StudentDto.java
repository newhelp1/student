package com.wyf.stu.dto;

import com.wyf.stu.entity.Student;
import lombok.Data;

@Data
public class StudentDto extends Student {

    private String collegeName;

    private String majorName;

    private String gradeName;

    private String classesName;

}
