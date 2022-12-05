package com.wyf.stu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StuApplication {

    public static void main(String[] args) {

        SpringApplication.run(StuApplication.class, args);
        log.info("启动成功");
    }
}
