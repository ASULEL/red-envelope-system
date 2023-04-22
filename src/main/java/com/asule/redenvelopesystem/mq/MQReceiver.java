package com.asule.redenvelopesystem.mq;

import com.asule.redenvelopesystem.service.RedRecordService;
import com.asule.redenvelopesystem.util.JsonUtil;
import com.asule.redenvelopesystem.vo.GrabRedEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
public class MQReceiver {


    @Resource
    private RedRecordService redRecordService;


    /**
     * 抢红包操作
     *
     * @param message
     */
    @RabbitListener(queues = "red-envelope-queue")
    public void receive(String message) {
        log.info("接收消息：" + message);
        GrabRedEnvelope redEnvelope = JsonUtil.jsonStr2Object(message, GrabRedEnvelope.class);
        //抢红包操作
        redRecordService.grab(redEnvelope);
    }
}
