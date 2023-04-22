package com.asule.redenvelopesystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/1/31 9:48
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true )
public class CommonResult<T> {

    private Integer code;
    private String message;
    private T data;

    public CommonResult(Integer code, String message) {
        this(code, message, null);
    }

    public static CommonResult success(){
        return new CommonResult(CommonResultEnum.SUCCESS.getCode(),CommonResultEnum.SUCCESS.getMessage(),null);
    }

    public static <T> CommonResult<T> success(T t){
        return new CommonResult(CommonResultEnum.SUCCESS.getCode(),CommonResultEnum.SUCCESS.getMessage(),t);
    }

    public static CommonResult error(CommonResultEnum commonResultEnum){
        return new CommonResult(commonResultEnum.getCode(),commonResultEnum.getMessage(),null);
    }

    public static <T> CommonResult<T> error(CommonResultEnum commonResultEnum,T t){
        return new CommonResult(commonResultEnum.getCode(),commonResultEnum.getMessage(),t);
    }
}
