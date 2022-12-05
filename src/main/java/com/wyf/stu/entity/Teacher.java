package com.wyf.stu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@TableName("sys_teacher")
@ApiModel(value = "Teacher对象", description = "")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("教师主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("教师账号")
    private String name;

    @ApiModelProperty("教师姓名")
    private String nickname;

    @ApiModelProperty("教师密码")
    private String password;

    @ApiModelProperty("教师编号")
    private String no;

    @ApiModelProperty("教师邮箱")
    private String email;

    @ApiModelProperty("教师手机")
    private String phone;

    @ApiModelProperty("教师住址")
    private String address;

    @ApiModelProperty("教师性别")
    private String sex;

    @ApiModelProperty("入职时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date creatTime;

    @ApiModelProperty("状态")
    private boolean status ;

    @TableField(exist = false)
    private String code;

    @TableField(exist = false)
    private List<Menu> menus;

    @TableField(exist = false)
    private String newPassword;


    @ApiModelProperty("头像")
    private String img ;

    @ApiModelProperty("角色")
    private String role ;
}
