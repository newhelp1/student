package com.wyf.stu.dto;

import lombok.Data;

@Data
public class AdminPasswordDto {

    private String name;
    private String password;
    private String newPassword;

    private String email;
    private String code;
}
