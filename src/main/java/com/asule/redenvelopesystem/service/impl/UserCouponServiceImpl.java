package com.asule.redenvelopesystem.service.impl;

import com.asule.redenvelopesystem.domain.*;
import com.asule.redenvelopesystem.service.CouponService;
import com.asule.redenvelopesystem.vo.GrabCoupon;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.asule.redenvelopesystem.service.UserCouponService;
import com.asule.redenvelopesystem.mapper.UserCouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
* @author 12707
* @description 针对表【user_coupon】的数据库操作Service实现
* @createDate 2023-04-21 10:31:44
*/
@Service
@Slf4j
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon>
    implements UserCouponService{

    @Resource
    private CouponService couponService;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCoupon grab(GrabCoupon grabCoupon) {
        String signal = grabCoupon.getSignal();
        User user = grabCoupon.getUser();
        Integer money = grabCoupon.getMoney();

        String lockKey = "grabCoupon_lock:" + signal; //定义分布式锁的key

        try {
            boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked");
            //获取锁失败,说明此次抢购红包没有成功，需要将抢到的金额重新push回redis中，数量也需要加回来
            if (!locked) {
                redisTemplate.opsForValue().increment("couPon:" + signal + ":total");
                return null;
            }
            //非自然情况,死锁 问题：什么时候回产生死锁
            redisTemplate.expire(lockKey, 10, TimeUnit.SECONDS); // 设置锁的过期时间，避免死锁

            //大红包扣减金额和数量
            try {
                //加上for update来实现Mysql悲观锁 写比较多，Mysql行锁
                //可以保证在查询红包信息后，对应的记录被锁定，其他线程无法修改
                Coupon coupon = couponService.getOne(new QueryWrapper<Coupon>()
                        .eq("id", signal)
                        .last("FOR UPDATE"));
                boolean flag = couponService.update(new UpdateWrapper<Coupon>()
                        .setSql("remaining_num=" + "remaining_num-1")
                        .eq("id", signal)
                        .gt("remaining_num", 0)
                );
                //假设没有更新成功,需要将抢到的金额重新push回redis中,数量也需要加回来
                if (!flag) {
                    redisTemplate.opsForValue().increment("couPon:" + signal + ":total");
                    return null;
                }
            } catch (Exception e) {
                //如果出现异常,需要将抢到的个数重新push回redis中，数量也需要加回来
                redisTemplate.opsForValue().increment("couPon:" + signal + ":total");
                e.printStackTrace();
            }

            //大红包扣减数量
            Coupon coupon = couponService.getById(signal);
//        BigDecimal remainMoney = redPacket.getRemainingAmount().subtract(money);
//        System.out.println(remainMoney);
//        redPacket.setRemainingAmount(remainMoney);
//        coupon.setRemainingNum(coupon.getRemainingNum() - 1);
//        couponService.updateById(coupon);

            //抢到红包的列表增加该用户抢到的值
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(user.getPhone())
                    .setCouponId(signal)
                    .setAmount(money)
                    .setStatus(0)
                    .setCreateTime(new Date())
                    .setEndTime(coupon.getEndTime());
            userCouponMapper.insert(userCoupon);
            redisTemplate.opsForValue().set("grabCoupon:" + user.getPhone() + ":" + signal, coupon);
            log.info("抢代金券红包成功,userCoupon:{}", coupon);
            return userCoupon;
        }finally {
            redisTemplate.delete(lockKey); // 释放锁
        }
    }
}




