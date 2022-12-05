package com.wyf.stu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.List;

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
 * @since 2022-10-21
 */
@Getter
@Setter
@TableName("sys_student")
@ApiModel(value = "Student对象", description = "")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("学生主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("学生学号")
    private String sno;

    @ApiModelProperty("学生账号")
    private String name;


    @ApiModelProperty("学生密码")
    private String password;

    @ApiModelProperty("学生姓名")
    private String nickname;

    @ApiModelProperty("学生性别")
    private String sex;

    @ApiModelProperty("学生邮箱")
    private String email;

    @ApiModelProperty("学生手机号")
    private String phone;

    @ApiModelProperty("学生住址")
    private String address;

    @ApiModelProperty("学院id")
    private Integer collegeId;

    @ApiModelProperty("专业id")
    private Integer majorId;

    @ApiModelProperty("年级id")
    private Integer gradeId;

    @ApiModelProperty("班级id")
    private Integer classesId;

    @ApiModelProperty("状态")
    private boolean status;

    @ApiModelProperty("头像")
    private String img;

    @TableField(exist = false)
    private String code;

    @ApiModelProperty("角色")
    private String role ;

    @TableField(exist = false)
    private List<Menu> menus;

    @TableField(exist = false)
    private String newPassword;


}
