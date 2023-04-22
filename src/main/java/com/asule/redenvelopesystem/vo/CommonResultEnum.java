package com.asule.redenvelopesystem.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 10:55
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Getter
@AllArgsConstructor
public enum CommonResultEnum {

    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),

    //登陆模块
    LOGIN_ERROR(500210,"用户名或密码格式不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    SESSION_ERROR(500213,"用户未登录"),

    //发红包模块 500300
    SIGNAL_REPEAT(500300, "该口令已经存在"),

    //抢红包模块 500400
    GRAB_REPEAT(500400, "已经抢过该红包"),
    RED_ENVELOPE_EMPTY(500401,"红包已经抢光" );

    //限流

    //发货模块

    //CDN模块

    private final Integer code;
    private final String message;
}
