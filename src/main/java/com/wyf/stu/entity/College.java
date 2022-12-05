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
 * @since 2022-09-15
 */
@Getter
@Setter
@TableName("sys_college")
@ApiModel(value = "College对象", description = "")
public class College implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("学院代码")
    private Integer code;

    @ApiModelProperty("学院名称")
    private String name;

    @ApiModelProperty("负责人")
    private String leader;

    @ApiModelProperty("父级id")
    private Integer pid;

    @TableField(exist = false)
    private List<College> children;
}
