package com.asule.redenvelopesystem.controller;

import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.service.UserService;
import com.asule.redenvelopesystem.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 12:15
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@RestController
@Slf4j
@RequestMapping("/user")
@Api(tags = "用户模块", value = "登录")
public class UserController {

    @Resource
    private UserService userService;


    @ApiOperation("登陆接口")
    @PostMapping(value = "/doLogin")
    public CommonResult doLogin(User user, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}", user);
        return userService.doLogin(user, request, response);
    }

}
