package com.asule.redenvelopesystem.util;

import java.util.UUID;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 12:57
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
public class UUIDUtil {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
