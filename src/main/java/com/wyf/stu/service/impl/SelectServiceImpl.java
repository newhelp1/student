package com.wyf.stu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wyf.stu.commons.Constants;
import com.wyf.stu.commons.Result;
import com.wyf.stu.entity.Select;
import com.wyf.stu.entity.Student;
import com.wyf.stu.mapper.SelectMapper;
import com.wyf.stu.service.ISelectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangyifan
 * @since 2022-09-18
 */
@Service
public class SelectServiceImpl extends ServiceImpl<SelectMapper, Select> implements ISelectService {

    @Resource
    private SelectMapper selectMapper;

    @Transactional
    @Override
    public Result saveAll(Integer majorId, List<Select> select) {


        LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Select::getMajorId,majorId);
        List<Select> selectList = list(queryWrapper);
        for (Select list : selectList) {
            if (list.getStudentId()==null){
                selectMapper.deleteById(list.getId());
            }
        }
//        remove(new UpdateWrapper<Select>().eq("major_id",majorId));
        saveBatch(select);
        return Result.success();
    }

    @Transactional
    @Override
    public Result saveStudentAll(Integer majorId,Integer id, List<Select> select) {
        LambdaQueryWrapper<Select> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Select::getMajorId,majorId);
        queryWrapper.eq(Select::getStudentId,id);
        remove(queryWrapper);
        saveBatch(select);
        return Result.success();
    }



}
