package com.asule.redenvelopesystem.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 11:16
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Configuration
//@EnableOpenApi // 开启 Swagger3,可不写,访问地址：http://ip:port/projectName/swagger-ui/index.html
public class SwaggerConfiguration {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select() // 通过.select()方法,去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))   //仅扫描类上有@Api注解的
//      .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) //仅扫描类上有@ApiOperation注解的
//      .apis(RequestHandlerSelectors.basePackage("com.qfx.controller"))        //只扫描该包下面的接口
                .paths(PathSelectors.any())                                             //任何请求都扫描
//      .paths(PathSelectors.regex("/test/.*"))                                 //匹配/test/开头的路径信息
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger3接口文档")                      // 标题
                .description("适用于前后端分离统一的接口文档")     // 描述
                .contact(new Contact("阿苏勒", "网址", "15067545052@163.com"))// 其他信息
                .version("1.0")                               // 版本
                .build();
    }
}
