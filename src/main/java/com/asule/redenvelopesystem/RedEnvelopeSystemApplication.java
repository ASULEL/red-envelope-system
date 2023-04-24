package com.asule.redenvelopesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@MapperScan("com.asule.redenvelopesystem.mapper")
@EnableOpenApi
@EnableAsync
public class RedEnvelopeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedEnvelopeSystemApplication.class, args);
    }

}
