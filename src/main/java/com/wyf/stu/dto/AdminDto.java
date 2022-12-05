package com.wyf.stu.dto;

import com.wyf.stu.entity.Admin;
import com.wyf.stu.entity.Menu;
import lombok.Data;

import java.util.List;

@Data
public class AdminDto extends Admin {

    private String name;

    private String password;

    private String nickname;

    private String email;

    private String code;

    private String img;
    //生成token
    private String token;

    private String role;

    private List<Menu> menus;

}
