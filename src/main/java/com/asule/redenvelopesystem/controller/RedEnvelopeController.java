package com.asule.redenvelopesystem.controller;

import com.asule.redenvelopesystem.service.RedPacketService;
import com.asule.redenvelopesystem.util.CookieUtil;
import com.asule.redenvelopesystem.vo.CommonResult;
import com.asule.redenvelopesystem.vo.RedEnvelopeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 13:56
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@RequestMapping("/red-envelop")
@RestController
@Slf4j
@Api(tags = "红包相关模块" , value = "发红包抢红包")
public class RedEnvelopeController {

    @Resource
    private RedPacketService redPacketService;


    @ApiOperation("发红包模块")
    @PostMapping("/sendRedEnvelope")
    public CommonResult sendRedEnvelope(HttpServletRequest request, RedEnvelopeVo redEnvelopeVo){
        //获取cookie
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        log.info("userTicket:{},redEnvelopeVo:{}",userTicket,redEnvelopeVo);
        return redPacketService.sendRedEnvelope(userTicket,redEnvelopeVo);
    }

    @ApiOperation("抢红包模块")
    @PostMapping("/grabRedEnvelope")
    public CommonResult grabRedEnvelope(HttpServletRequest request, String signal,Integer type){
        //获取cookie
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        log.info("userTicket:{},signal:{}",userTicket,signal);
        if (type == 1)
            return redPacketService.grabNormalRedEnvelope(userTicket,signal);
        return redPacketService.grabRedEnvelope(userTicket,signal);
    }


}
