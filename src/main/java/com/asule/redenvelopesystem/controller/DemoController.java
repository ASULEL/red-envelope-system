package com.asule.redenvelopesystem.controller;

import com.asule.redenvelopesystem.vo.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/21 11:03
 * @Version: 1.0
 * @Description:
 */
@RestController
@Api(value = "测试类相关信息", tags = "demo测试类")
public class DemoController {


    /**
     * test
     *
     * @return
     */
    @GetMapping("hello")
    @ApiOperation("最简单的问候")
    public CommonResult<String> hello() {
        return CommonResult.success("hello world");
    }

    @ApiOperation(value = "稍微高级一点的问候", notes = "升级版")
    @ApiImplicitParam(name = "name",value = "用户名称")
    @GetMapping("hello/{name}")
    public CommonResult<String> hello(@PathVariable String name) {
        return CommonResult.success(name + "hello world");
    }

    @ApiOperation(value = "高级问候",notes = "最终版")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "age",value = "年龄"),
            @ApiImplicitParam(name = "msg",value = "信息", required = false)
    })
    @GetMapping("helloExt/{name}/{age}")
    public CommonResult<String> hello(@PathVariable String name,@PathVariable int age,String msg) {
        return CommonResult.success(name + "你好，你今年" + age + "吗？你带来的消息是" + msg);
    }
}
