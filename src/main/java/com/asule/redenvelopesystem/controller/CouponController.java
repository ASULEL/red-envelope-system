package com.asule.redenvelopesystem.controller;

import com.asule.redenvelopesystem.domain.Coupon;
import com.asule.redenvelopesystem.service.CouponService;
import com.asule.redenvelopesystem.util.CookieUtil;
import com.asule.redenvelopesystem.vo.CommonResult;
import com.asule.redenvelopesystem.vo.CouponVo;
import com.asule.redenvelopesystem.vo.RedEnvelopeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/coupon")
@Api(tags = "代金券相关模块" , value = "发代金券抢代金券")
public class CouponController {
    @Resource
    private CouponService couponService;

    @ApiOperation("发代金券模块")
    @PostMapping("/sendCoupon")
    public CommonResult sendCoupon(HttpServletRequest request, CouponVo couponVo){
        //获取cookie
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        log.info("userTicket:{},redEnvelopeVo:{}",userTicket,couponVo);
        return couponService.sendCoupon(userTicket,couponVo);
    }

    @ApiOperation("抢代金券模块")
    @PostMapping("/grabCoupon")
    public CommonResult grabRedEnvelope(HttpServletRequest request, String signal){
        //获取cookie
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        log.info("userTicket:{},signal:{}",userTicket,signal);
        return couponService.grabCoupon(userTicket,signal);
    }

}
