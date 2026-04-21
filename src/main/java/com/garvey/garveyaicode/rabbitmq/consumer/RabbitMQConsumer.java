package com.garvey.garveyaicode.rabbitmq.consumer;

import com.garvey.garveyaicode.common.config.RabbitMQConfig;
import com.garvey.garveyaicode.model.entity.User;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    /**
     * 监听指定队列，接收消息
     * @RabbitListener：指定要监听的队列名称
     * @param message 接收到的消息内容
     */
    @RabbitListener(queues = RabbitMQConfig.TEST_QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println("消费者接收到消息：" + message);
        // 这里可以编写业务逻辑（如入库、调用接口等）
    }

    // 可选：接收对象消息
//    @RabbitListener(queues = RabbitMQConfig.TEST_QUEUE_NAME)
    public void receiveObjectMessage(User user) {
        System.out.println("消费者接收到对象消息：" + user);
    }
}