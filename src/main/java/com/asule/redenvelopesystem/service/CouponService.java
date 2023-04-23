package com.asule.redenvelopesystem.service;

import com.asule.redenvelopesystem.domain.Coupon;
import com.asule.redenvelopesystem.vo.CommonResult;
import com.asule.redenvelopesystem.vo.CouponVo;
import com.asule.redenvelopesystem.vo.RedEnvelopeVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 12707
* @description 针对表【coupon】的数据库操作Service
* @createDate 2023-04-21 10:31:41
*/
public interface CouponService extends IService<Coupon> {

    CommonResult sendCoupon(String userTicket, CouponVo couponVo);

    CommonResult grabCoupon(String userTicket, String signal);
}
