package com.wyf.stu.service;

import com.wyf.stu.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangyifan
 * @since 2022-10-26
 */
public interface IMenuService extends IService<Menu> {


    List<Menu> findMenus(String name);

}
