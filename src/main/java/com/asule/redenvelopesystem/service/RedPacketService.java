package com.asule.redenvelopesystem.service;

import com.asule.redenvelopesystem.domain.RedPacket;
import com.asule.redenvelopesystem.vo.CommonResult;
import com.asule.redenvelopesystem.vo.RedEnvelopeVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 12707
* @description 针对表【red_packet】的数据库操作Service
* @createDate 2023-04-21 10:28:26
*/
public interface RedPacketService extends IService<RedPacket> {

    CommonResult sendRedEnvelope(String userTicket, RedEnvelopeVo redEnvelopeVo);

    CommonResult grabRedEnvelope(String userTicket, String signal);
}
