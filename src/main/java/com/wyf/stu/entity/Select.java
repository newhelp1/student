package com.wyf.stu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-18
 */
@Getter
@Setter
@TableName("sys_select")
@ApiModel(value = "Select对象", description = "")
public class Select implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("选课主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("学生id")
    private Integer studentId;

    @ApiModelProperty("专业id")
    private Integer majorId;

    @ApiModelProperty("课程id")
    private Integer courseId;

    @ApiModelProperty("年级id")
    private Integer gradeId;

    @ApiModelProperty("班级id")
    private Integer classesId;

    @ApiModelProperty("星期几")
    private String weekDay;

    @ApiModelProperty("第几节课")
    private String section;
}
