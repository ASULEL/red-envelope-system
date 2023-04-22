package com.asule.redenvelopesystem.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 简要描述
 *
 * @Author: ASuLe
 * @Date: 2023/4/22 10:43
 * @Version: 1.0
 * @Description: 文件作用详细描述....
 */
@Configuration
public class RabbitMQTopicConfig {

    //定义一个交换机
    public static final String EXCHANGE_RED_ENVELOPE = "red-envelope-exchange";

    //定义队列
    public static final String QUEUE = "red-envelope-queue";

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE_RED_ENVELOPE);
    }

    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }

    // 一个交换机可以绑定多个消息队列，也就是消息通过一个交换机，可以分发到不同的队列当中去。
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with("redEnvelope.#");
    }


}
