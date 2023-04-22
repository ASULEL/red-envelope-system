package com.asule.redenvelopesystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 15:22
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedEnvelopeVo {

    /**
     * 红包总金额
     */
    private BigDecimal totalAmount;

    /**
     * 红包个数
     */
    private Integer totalNum;
    /**
     * 用户设置的口令
     */
    private Integer signal;
    /**
     * 红包类型：1-普通红包，2-拼手气红包
     */
    private Integer type;
}
