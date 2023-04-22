package com.asule.redenvelopesystem.service.impl;

import com.asule.redenvelopesystem.domain.RedPacket;
import com.asule.redenvelopesystem.domain.RedRecord;
import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.mapper.RedRecordMapper;
import com.asule.redenvelopesystem.service.RedPacketService;
import com.asule.redenvelopesystem.service.RedRecordService;
import com.asule.redenvelopesystem.vo.GrabRedEnvelope;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

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

        //大红包扣减金额和数量
        RedPacket redPacket = redPacketService.getById(signal);
        BigDecimal remainMoney = redPacket.getRemainingAmount().subtract(money);
        System.out.println(remainMoney);
        redPacket.setRemainingAmount(remainMoney);
        redPacket.setRemainingNum(redPacket.getRemainingNum() - 1);
        redPacketService.updateById(redPacket);

        //抢到红包的列表增加该用户抢到的值
        RedRecord redRecord = new RedRecord();
        redRecord.setUserId(user.getPhone())
                .setRedPacketId(signal)
                .setAmount(money)
                .setCreatetime(new Date());
        redRecordMapper.insert(redRecord);
        redisTemplate.opsForValue().set("grab:" + user.getPhone() + ":" + signal, redRecord);
        log.info("抢红包成功,redRecord:{}",redRecord);
        return redRecord;
    }
}




