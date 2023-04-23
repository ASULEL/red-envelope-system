package com.asule.redenvelopesystem.mq;

import com.asule.redenvelopesystem.domain.RedRecord;
import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.domain.UserCoupon;
import com.asule.redenvelopesystem.service.RedRecordService;
import com.asule.redenvelopesystem.service.UserCouponService;
import com.asule.redenvelopesystem.util.JsonUtil;
import com.asule.redenvelopesystem.vo.GrabCoupon;
import com.asule.redenvelopesystem.vo.GrabRedEnvelope;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/22 11:02
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Component
@Slf4j
public class MQReceiver {


    @Resource
    private RedRecordService redRecordService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserCouponService userCouponService;



    /**
     * 抢红包操作
     *
     * @param message
     */
    @RabbitListener(queues = "red-envelope-queue")
    public void receive(String message) {

        log.info("接收消息：" + message);
        GrabRedEnvelope redEnvelope = JsonUtil.jsonStr2Object(message, GrabRedEnvelope.class);
        String signal = redEnvelope.getSignal();
        User user = redEnvelope.getUser();
        BigDecimal money = redEnvelope.getMoney();
        //判断是否重复抢购
        RedRecord redRecord = redRecordService.getOne(new QueryWrapper<RedRecord>()
                .eq("red_packet_id", signal)
                .eq("user_id", user.getPhone())
        );
        if ( redRecord != null){
            if (redEnvelope.getType() == 2)
                redisTemplate.opsForList().rightPush("redPocket:" + signal + ":list", money.multiply(new BigDecimal(100)).intValue());
            redisTemplate.opsForValue().increment("redPocket:" + signal + ":total");
            return;
        }

        //抢红包操作
        redRecordService.grab(redEnvelope);
    }

    /**
     * 抢代金券红包操作
     *
     * @param message
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "coupon-queue"),
                    exchange = @Exchange(name = "coupon-exchange",type = ExchangeTypes.TOPIC),
                    key = "coupon.#"
            )
    )
    public void receiveCoupon(String message) {
        log.info("接收消息：" + message);
        GrabCoupon grabCoupon = JsonUtil.jsonStr2Object(message, GrabCoupon.class);

        String signal = grabCoupon.getSignal();
        User user = grabCoupon.getUser();
        Integer money = grabCoupon.getMoney();
        //判断是否重复抢购
        UserCoupon userCoupon = userCouponService.getOne(new QueryWrapper<UserCoupon>()
                .eq("coupon_id", signal)
                .eq("user_id", user.getPhone())
        );
        if ( userCoupon != null){
            redisTemplate.opsForValue().increment("couPon:" + signal + ":total");
            return;
        }

        //抢红包操作
        userCouponService.grab(grabCoupon);
    }

}
