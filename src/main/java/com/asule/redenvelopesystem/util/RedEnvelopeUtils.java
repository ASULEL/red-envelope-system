package com.asule.redenvelopesystem.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 13:52
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
public class RedEnvelopeUtils {
    /**
     * 发红包算法，金额参数以分为单位
     *
     * @param moneyTotal 金额总数
     * @param amount     总人数
     * @return
     */
    public static List<Integer> divideRedPackage(Integer moneyTotal, Integer amount) {
        List<Integer> lists = new ArrayList<>();
        Integer amountBZ = amount;

        Random random = new Random();
        if (moneyTotal > 0 && amount > 0) {
            for (int i = 0; i < amountBZ - 1; i++) {
                int redPocket = random.nextInt(moneyTotal / amount * 2) + 1;
                moneyTotal -= redPocket;
                amount--;
                lists.add(redPocket);
            }
            //循环结束，剩余的金额作为最后一个随机金额，加入到列表中
            lists.add(moneyTotal);
        }
        return lists;
    }
}
