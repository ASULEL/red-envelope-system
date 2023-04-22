package com.asule.redenvelopesystem.exception;

import com.asule.redenvelopesystem.vo.CommonResult;
import com.asule.redenvelopesystem.vo.CommonResultEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 12:39
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 *
 *
 * RestControllerAdvice+ExceptionHandler这两个注解的组合，被用作项目的全局异常处理，
 * 一旦项目中发生了异常，就会进入使用了RestControllerAdvice注解类中使用了ExceptionHandler注解的方法
 *

 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public CommonResult ExceptionHandler(Exception e){
        if ( e instanceof GlobalException){
            GlobalException exception = (GlobalException) e;
            return CommonResult.error(exception.getCommonResultEnum());
        }else if ( e instanceof BindException){
            BindException bindException = (BindException) e;
            CommonResult commonResult = CommonResult.error(CommonResultEnum.BIND_ERROR);
            commonResult.setMessage("参数校验异常:" + bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return commonResult;
        }
        System.out.println("异常信息：" + e);
        return CommonResult.error(CommonResultEnum.ERROR);
    }
}
