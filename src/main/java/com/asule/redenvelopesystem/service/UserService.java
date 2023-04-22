package com.asule.redenvelopesystem.service;

import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.vo.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @author 12707
* @description 针对表【user】的数据库操作Service
* @createDate 2023-04-21 10:31:52
*/
public interface UserService extends IService<User> {

    CommonResult doLogin(User user, HttpServletRequest request, HttpServletResponse response);
}
