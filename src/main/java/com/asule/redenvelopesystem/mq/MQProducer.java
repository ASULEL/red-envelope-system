package com.asule.redenvelopesystem.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
public class MQProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendGrabRedEnvelope(String message) {
        log.info("发送消息" + message);
        rabbitTemplate.convertAndSend("red-envelope-exchange", "redEnvelope.message", message);
    }
    public void sendGrabCoupon(String message) {
        log.info("发送消息" + message);
        rabbitTemplate.convertAndSend("coupon-exchange", "coupon.message", message);
    }
}
