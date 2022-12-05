package com.wyf.stu.service;

import com.wyf.stu.commons.Result;
import com.wyf.stu.entity.Select;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wyf.stu.entity.Student;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-18
 */
public interface ISelectService extends IService<Select> {

    Result saveAll(Integer majorId, List<Select> select);

    Result saveStudentAll(Integer majorId,Integer id, List<Select> select);

}
