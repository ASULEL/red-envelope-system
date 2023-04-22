package com.asule.redenvelopesystem.service.impl;

import com.asule.redenvelopesystem.domain.RedPacket;
import com.asule.redenvelopesystem.domain.RedRecord;
import com.asule.redenvelopesystem.domain.User;
import com.asule.redenvelopesystem.exception.GlobalException;
import com.asule.redenvelopesystem.mapper.RedPacketMapper;
import com.asule.redenvelopesystem.mq.MQProducer;
import com.asule.redenvelopesystem.service.RedPacketService;
import com.asule.redenvelopesystem.util.JsonUtil;
import com.asule.redenvelopesystem.util.RedEnvelopeUtils;
import com.asule.redenvelopesystem.vo.CommonResult;
import com.asule.redenvelopesystem.vo.CommonResultEnum;
import com.asule.redenvelopesystem.vo.GrabRedEnvelope;
import com.asule.redenvelopesystem.vo.RedEnvelopeVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 12707
 * @description 针对表【red_packet】的数据库操作Service实现
 * @createDate 2023-04-21 10:28:26
 */
@Service
public class RedPacketServiceImpl extends ServiceImpl<RedPacketMapper, RedPacket>
        implements RedPacketService, InitializingBean {

    @Resource
    private RedPacketMapper redPacketMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisScript<List> script1;

    @Resource
    private MQProducer mqProducer;

    /**
     * 发送红包整体
     *
     * @param userTicket
     * @param redEnvelopeVo
     * @return
     */
    @Override
    public CommonResult sendRedEnvelope(String userTicket, RedEnvelopeVo redEnvelopeVo) {
        //1.根据cookie获取发红包的用户信息
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user == null)
            throw new GlobalException(CommonResultEnum.SESSION_ERROR);
        //2.获取红包口令
        String signal = getSignal(redEnvelopeVo.getSignal());
        Integer money = (Integer) redisTemplate.opsForValue().get("redPacket:" + signal + ":total");
        if (money != null)
            throw new GlobalException(CommonResultEnum.SIGNAL_REPEAT);
        //3.根据红包类型选择发送
        if (redEnvelopeVo.getType() == 1) {
            return NoLuckLuckRedEnvelope();
        } else {
            return LuckRedEnvelope(user, redEnvelopeVo, signal);
        }
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
        int feed = random.nextInt();
        char prefix = feed == 0 ? big : small;
        return prefix + "-" + signal;
    }

    /**
     * 发送普通红包
     *
     * @return
     */
    private CommonResult NoLuckLuckRedEnvelope() {
        return null;
    }


    /**
     * 发送拼手气红包
     *
     * @param user
     * @param redEnvelopeVo
     * @param signal
     * @return
     */
    private CommonResult LuckRedEnvelope(User user, RedEnvelopeVo redEnvelopeVo, String signal) {
        Integer money = multiplyMoney(redEnvelopeVo.getTotalAmount());
        List<Integer> list = RedEnvelopeUtils.divideRedPackage(money, redEnvelopeVo.getTotalNum());
        //1.数据异步入mysql
        addRedEnvelope(redEnvelopeVo, signal, user);
        //2.总数和列表入redis
        String prefix = "redPocket:" + signal;
        redisTemplate.opsForValue().set(prefix + ":total", redEnvelopeVo.getTotalNum());
        redisTemplate.opsForList().leftPushAll(prefix + ":list", list);
        return CommonResult.success(signal);
    }

    /**
     * 将元化成分
     */
    private Integer multiplyMoney(BigDecimal money) {
        return money.multiply(new BigDecimal(100)).intValue();
    }


    /**
     * @param redEnvelopeVo
     * @param signal
     * @param user
     * @return
     */
    @Async
    public boolean addRedEnvelope(RedEnvelopeVo redEnvelopeVo, String signal, User user) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        Date after24h = calendar.getTime();
        RedPacket redPacket = new RedPacket();
        redPacket.setId(signal)
                .setUserId(user.getPhone())
                .setTotalAmount(redEnvelopeVo.getTotalAmount())
                .setTotalNum(redEnvelopeVo.getTotalNum())
                .setRemainingAmount(redEnvelopeVo.getTotalAmount())
                .setRemainingNum(redEnvelopeVo.getTotalNum())
                .setCreateTime(now)
                .setExpireTime(after24h)
                .setType(redEnvelopeVo.getType());
        return redPacketMapper.insert(redPacket) == 1;
    }


    /**
     * 抢红包
     *
     * @param userTicket
     * @param signal
     * @return
     */
    @Override
    public CommonResult grabRedEnvelope(String userTicket, String signal) {
        //1.判断是否重复抢红包
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user == null)
            throw new GlobalException(CommonResultEnum.SESSION_ERROR);
        RedRecord redRecord = (RedRecord) redisTemplate.opsForValue().get("grab:" + user.getPhone() + ":" + signal);
        if (redRecord != null)
            return CommonResult.error(CommonResultEnum.GRAB_REPEAT,redRecord);
        //2.内存标记，这里先不做，这是本地缓存
        //3.预减库存
        String prefix = "redPocket:" + signal;
        List<String> list = Arrays.asList(prefix + ":total", prefix + ":list");
        List result = (List) redisTemplate.execute(script1, list, Collections.EMPTY_LIST);
        Long stock = (Long) result.get(0);
        Long bigMoney = (Long) result.get(1);
        BigDecimal money = new BigDecimal(bigMoney).divide(new BigDecimal(100));
        if (stock == 0)
            return CommonResult.error(CommonResultEnum.RED_ENVELOPE_EMPTY);
        //4.发送给消息队列减数据库存和金额
        //异步处理，前期大量请求过来可以快速处理，后面消息队列再去慢慢处理，流量削峰
        GrabRedEnvelope grabRedEnvelope = new GrabRedEnvelope(signal, user, money);
        mqProducer.sendGrabRedEnvelope(JsonUtil.object2JsonStr(grabRedEnvelope));
        return CommonResult.success(money);
    }

    /**
     * 系统初始化，把红包和金额加载到redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

    }
}




