package com.wyf.stu.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//接口统一包装返回类型
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private String code;
    private String message;
    private Object data;

    public static Result success(){
        return new Result(Constants.CODE_200,"",null);
    }

    public static Result success(Object data){
        return new Result(Constants.CODE_200,"",data);
    }
    public static Result error(String code,String message){
        return new Result(code,message,null);
    }

    public static Result error(){
        return new Result(Constants.CODE_500,"系统忙",null);
    }


}
