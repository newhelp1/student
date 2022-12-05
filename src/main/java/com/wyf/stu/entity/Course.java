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
 * @since 2022-09-16
 */
@Getter
@Setter
@TableName("sys_course")
@ApiModel(value = "Course对象", description = "")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("课程主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("课程名称")
    private String name;

    @ApiModelProperty("课程编号")
    private String no;

    @ApiModelProperty("上课地点")
    private String room;
//
//    @ApiModelProperty("课程id")
//    private Integer majorId;
//
    @ApiModelProperty("是否必修")
    private String type;

    @ApiModelProperty("教师id")
    private Integer teacherId;


}
