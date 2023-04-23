package com.asule.redenvelopesystem.vo;


import com.asule.redenvelopesystem.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrabCoupon {
    /**
     * 红包口令
     */
    private String signal;

    /**
     * 用户
     */
    private User user;

}
