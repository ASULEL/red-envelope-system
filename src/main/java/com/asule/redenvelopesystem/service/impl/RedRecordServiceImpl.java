package com.asule.redenvelopesystem.service.impl;

import com.asule.redenvelopesystem.domain.RedPacket;
import com.asule.redenvelopesystem.domain.RedRecord;
import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.mapper.RedRecordMapper;
import com.asule.redenvelopesystem.service.RedPacketService;
import com.asule.redenvelopesystem.service.RedRecordService;
import com.asule.redenvelopesystem.vo.GrabRedEnvelope;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 12707
 * @description 针对表【red_record】的数据库操作Service实现
 * @createDate 2023-04-21 10:31:38
 */
@Service
@Slf4j
public class RedRecordServiceImpl extends ServiceImpl<RedRecordMapper, RedRecord>
        implements RedRecordService {

    @Resource
    private RedRecordMapper redRecordMapper;

    @Resource
    private RedPacketService redPacketService;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RedRecord grab(GrabRedEnvelope redEnvelope) {
        String signal = redEnvelope.getSignal();
        User user = redEnvelope.getUser();
        BigDecimal money = redEnvelope.getMoney();

        String lockKey = "grab_lock:" + signal; //定义分布式锁的key
        try {
            boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked");
            //获取锁失败,说明此次抢购红包没有成功，需要将抢到的金额重新push回redis中，数量也需要加回来
            if (!locked) {
                if (redEnvelope.getType() == 2)
                    redisTemplate.opsForList().rightPush("redPocket:" + signal + ":list", money.multiply(new BigDecimal(100)).intValue());
                redisTemplate.opsForValue().increment("redPocket:" + signal + ":total");
                return null;
            }
            //非自然情况,死锁 问题：什么时候回产生死锁
            redisTemplate.expire(lockKey, 10, TimeUnit.SECONDS); // 设置锁的过期时间，避免死锁

            //大红包扣减金额和数量
            try {
                //加上for update来实现Mysql悲观锁 写比较多，Mysql行锁
                //可以保证在查询红包信息后，对应的记录被锁定，其他线程无法修改
                RedPacket redPacket = redPacketService.getOne(new QueryWrapper<RedPacket>()
                        .eq("id", signal)
                        .last("FOR UPDATE"));
                BigDecimal remainMoney = redPacket.getRemainingAmount().subtract(money);
                boolean flag = redPacketService.update(new UpdateWrapper<RedPacket>()
                        .setSql("remaining_amount=" + remainMoney)
                        .setSql("remaining_num=" + "remaining_num-1")
                        .eq("id", signal)
                        .gt("remaining_num", 0)
                );
                //假设没有更新成功,需要将抢到的金额重新push回redis中,数量也需要加回来
                if (!flag) {
                    if (redEnvelope.getType() == 2)
                        redisTemplate.opsForList().rightPush("redPocket:" + signal + ":list", money.multiply(new BigDecimal(100)).intValue());
                    redisTemplate.opsForValue().increment("redPocket:" + signal + ":total");
                    return null;
                }
            } catch (Exception e) {
                //如果出现异常,需要将抢到的金额重新push回redis中，数量也需要加回来
                if (redEnvelope.getType() == 2)
                    redisTemplate.opsForList().rightPush("redPocket:" + signal + ":list", money.multiply(new BigDecimal(100)).intValue());
                redisTemplate.opsForValue().increment("redPocket:" + signal + ":total");
                e.printStackTrace();
            }

            //抢到红包的列表增加该用户抢到的值
            RedRecord redRecord = new RedRecord();
            redRecord.setUserId(user.getPhone())
                    .setRedPacketId(signal)
                    .setAmount(money)
                    .setCreatetime(new Date());

            //如果有一个人正好同时发送了两个请求过来，可能会出现一个人抢购到两个商品
            //简单有效的方法加一个唯一索引，将红包标识和phone绑成一个索引
            redRecordMapper.insert(redRecord);
            redisTemplate.opsForValue().set("grab:" + user.getPhone() + ":" + signal, redRecord);
            log.info("抢红包成功,redRecord:{}", redRecord);
            return redRecord;
        } finally {
            redisTemplate.delete(lockKey); // 释放锁
        }
    }
}




