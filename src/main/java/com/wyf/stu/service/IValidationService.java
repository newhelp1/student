package com.wyf.stu.service;

import cn.hutool.core.date.DateTime;
import com.wyf.stu.entity.Validation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-15
 */
public interface IValidationService extends IService<Validation> {
    void saveCode(String ademail, String code, Integer type, DateTime expireDate);
}
