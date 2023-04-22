package com.asule.redenvelopesystem.service.impl;

import com.asule.redenvelopesystem.exception.GlobalException;
import com.asule.redenvelopesystem.util.CookieUtil;
import com.asule.redenvelopesystem.util.UUIDUtil;
import com.asule.redenvelopesystem.vo.CommonResult;
import com.asule.redenvelopesystem.vo.CommonResultEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.service.UserService;
import com.asule.redenvelopesystem.mapper.UserMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 12707
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-04-21 10:31:52
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public CommonResult doLogin(User user, HttpServletRequest request, HttpServletResponse response) {
        String phone = user.getPhone();
        String password = user.getPassword();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user1 = userMapper.selectOne(queryWrapper);
        System.out.println(user1);
        if (user == null)
            throw new GlobalException(CommonResultEnum.LOGIN_ERROR);

        //判断密码是否正确
        if (!password.equals(user1.getPassword()))
            throw new GlobalException(CommonResultEnum.LOGIN_ERROR);

        //生成Cookie
        String userTicket = UUIDUtil.uuid();

        //将用户信息存入redis
        redisTemplate.opsForValue().set("user:" + userTicket, user);
        CookieUtil.setCookie(request,response,"userTicket",userTicket);
        return CommonResult.success(userTicket);
    }
}




