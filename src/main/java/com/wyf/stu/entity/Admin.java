package com.wyf.stu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 *
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-14
 */

@TableName("sys_admin")
@ApiModel(value = "Admin对象", description = "")
@ToString
@Data
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("管理员编号")
    private String no;

    @ApiModelProperty("管理员账号")
    private String name;

    @ApiModelProperty("管理员性别")
    private String sex;
    @ApiModelProperty("管理员密码")
    private String password;

    @ApiModelProperty("管理员姓名")
    private String nickname;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机")
    private String phone;

    @ApiModelProperty("头像")
    private String img;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("状态")
    private boolean status ;

    @ApiModelProperty("角色")
    private String role ;
}
