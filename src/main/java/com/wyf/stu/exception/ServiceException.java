package com.wyf.stu.exception;

import lombok.Getter;

//自定义异常
@Getter
public class ServiceException extends RuntimeException {

    private String code;

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }
}
