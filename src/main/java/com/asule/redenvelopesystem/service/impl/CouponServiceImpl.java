package com.asule.redenvelopesystem.service.impl;

import com.asule.redenvelopesystem.domain.Coupon;
import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.exception.GlobalException;
import com.asule.redenvelopesystem.mapper.CouponMapper;
import com.asule.redenvelopesystem.mq.MQProducer;
import com.asule.redenvelopesystem.service.CouponService;
import com.asule.redenvelopesystem.util.JsonUtil;
import com.asule.redenvelopesystem.vo.CommonResult;
import com.asule.redenvelopesystem.vo.CommonResultEnum;
import com.asule.redenvelopesystem.vo.CouponVo;
import com.asule.redenvelopesystem.vo.GrabCoupon;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
* @author 12707
* @description 针对表【coupon】的数据库操作Service实现
* @createDate 2023-04-21 10:31:41
*/
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon>
    implements CouponService{

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisScript<List> script2;

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private MQProducer mqProducer;


    @Override
    public CommonResult sendCoupon(String userTicket, CouponVo couponVo) {
        //1.根据cookie获取发红包的用户信息
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user == null)
            throw new GlobalException(CommonResultEnum.SESSION_ERROR);
        //2.获取红包口令
        String signal = getSignal(couponVo.getSignal());
        Integer money = (Integer) redisTemplate.opsForValue().get("couPon:" + signal + ":total");
        if (money != null)
            throw new GlobalException(CommonResultEnum.SIGNAL_REPEAT);
        //3.根据红包类型选择发送
            return sendNormalCoupon(user, couponVo, signal);
    }

    private CommonResult sendNormalCoupon(User user, CouponVo couponVo, String signal) {
        //1.数据异步入mysql
        addCouPon(couponVo, signal, user);
        //2.总数和列表入redis
        String prefix = "couPon:" + signal;
        redisTemplate.opsForValue().set(prefix + ":total", couponVo.getTotalNum(),24, TimeUnit.HOURS);
        return CommonResult.success(signal);
    }

    private boolean addCouPon(CouponVo couponVo, String signal, User user) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        Date after24h = calendar.getTime();
        Coupon coupon = new Coupon();
        coupon.setId(signal)
                .setUserId(user.getPhone())
                .setAmount(couponVo.getAmount())
                .setTotalNum(couponVo.getTotalNum())
                .setRemainingNum(couponVo.getTotalNum())
                .setConditions(couponVo.getCondition())
                .setEndTime(after24h)
                .setCreateTime(now);
        return couponMapper.insert(coupon) == 1;
    }


    @Override
    public CommonResult grabCoupon(String userTicket, String signal) {
        //1.判断是否重复抢红包
        System.out.println(userTicket);
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user == null)
            throw new GlobalException(CommonResultEnum.SESSION_ERROR);
        Coupon coupon1 = (Coupon) redisTemplate.opsForValue().get("grabCoupon:" + user.getPhone() + ":" + signal);
        if (coupon1 != null)
            return CommonResult.error(CommonResultEnum.GRAB_REPEAT,coupon1);
        //2.内存标记，这里先不做，这是本地缓存
        //3.预减库存
        String prefix = "couPon:" + signal;
        List<String> list = Arrays.asList(prefix + ":total");
        List result = (List) redisTemplate.execute(script2, list, Collections.EMPTY_LIST);
        Long stock = (Long) result.get(0);
        //从数据库获取对应设置的红包金额
        if (stock == 0)
            return CommonResult.error(CommonResultEnum.RED_ENVELOPE_EMPTY);
        //4.发送给消息队列减数据库存和金额
        //异步处理，前期大量请求过来可以快速处理，后面消息队列再去慢慢处理，流量削峰
        GrabCoupon grabCoupon = new GrabCoupon(signal, user);
        mqProducer.sendGrabCoupon(JsonUtil.object2JsonStr(grabCoupon));
        return CommonResult.success(0);
    }


    /**
     * ASCII随机转出一个英文字母
     *
     * @param signal
     * @return
     */
    private String getSignal(Integer signal) {
        Random random = new Random();
        char big = (char) (random.nextInt(26) + 65);
        char small = (char) (random.nextInt(26) + 97);
        int feed = random.nextInt(2);
        char prefix = feed == 0 ? big : small;
        return prefix + "-" + signal;
    }



}




