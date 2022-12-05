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
 * @since 2022-09-17
 */
@Getter
@Setter
@TableName("sys_classes")
@ApiModel(value = "Classes对象", description = "")
public class Classes implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("班级主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("班级代码")
    private String no;

    @ApiModelProperty("班级名称")
    private String name;

}
