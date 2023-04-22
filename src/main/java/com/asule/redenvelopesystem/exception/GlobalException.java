package com.asule.redenvelopesystem.exception;

import com.asule.redenvelopesystem.vo.CommonResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 12:38
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Data
@AllArgsConstructor
public class GlobalException extends RuntimeException{

    private CommonResultEnum commonResultEnum;


}
