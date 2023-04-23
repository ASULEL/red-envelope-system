package com.asule.redenvelopesystem.service;

import com.asule.redenvelopesystem.domain.UserCoupon;
import com.asule.redenvelopesystem.vo.GrabCoupon;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 12707
* @description 针对表【user_coupon】的数据库操作Service
* @createDate 2023-04-21 10:31:44
*/
public interface UserCouponService extends IService<UserCoupon> {

    UserCoupon grab(GrabCoupon grabCoupon);
}
