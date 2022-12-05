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
  @TableName("sys_grade")
@ApiModel(value = "Grade对象", description = "")
public class Grade implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("年级主键")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("年级")
      private String name;
}
