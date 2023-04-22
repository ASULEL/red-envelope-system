package com.asule.redenvelopesystem.vo;

import com.asule.redenvelopesystem.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/22 11:30
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrabRedEnvelope {

    /**
     * 红包口令
     */
    private String signal;

    /**
     * 用户
     */
    private User user;

    /**
     * 抢到的金额
     */
    private BigDecimal money;

}
