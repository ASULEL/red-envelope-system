package com.asule.redenvelopesystem.service;

import com.asule.redenvelopesystem.domain.RedRecord;
import com.asule.redenvelopesystem.vo.GrabRedEnvelope;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 12707
* @description 针对表【red_record】的数据库操作Service
* @createDate 2023-04-21 10:31:38
*/
public interface RedRecordService extends IService<RedRecord> {

    RedRecord grab(GrabRedEnvelope redEnvelope);
}
