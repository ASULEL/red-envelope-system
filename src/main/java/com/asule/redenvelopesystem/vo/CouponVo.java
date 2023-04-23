package com.asule.redenvelopesystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CouponVo {
    /**
     * 代金券总金额
     */
    private Integer amount;

    /**
     * 代金券红包个数
     */
    private Integer totalNum;
    /**
     * 用户设置的口令
     */
    private Integer signal;

    /**
     * 使用条件
     */

    private String condition;

}
