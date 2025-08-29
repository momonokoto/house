package com.zpark;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
@MapperScan("com.zpark.mapper")
public class AutoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoApplication.class, args);
    }
}
