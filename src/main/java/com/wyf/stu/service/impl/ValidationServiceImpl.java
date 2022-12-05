package com.wyf.stu.service.impl;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wyf.stu.entity.Validation;
import com.wyf.stu.mapper.ValidationMapper;
import com.wyf.stu.service.IValidationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-15
 */
@Service
public class ValidationServiceImpl extends ServiceImpl<ValidationMapper, Validation> implements IValidationService {

    @Transactional
    @Override
    public void saveCode(String email, String code, Integer type, DateTime expireDate) {
        Validation validation = new Validation();
        validation.setEmail(email);
        validation.setCode(code);
        validation.setType(type);
        validation.setTime(expireDate);

//        删除同类型的验证
        UpdateWrapper<Validation> adminUpdateWrapper = new UpdateWrapper<>();
        adminUpdateWrapper.eq("email",email);
        adminUpdateWrapper.eq("type", type);
        remove(adminUpdateWrapper);
        //在添加新的code
        save(validation);
    }
}
