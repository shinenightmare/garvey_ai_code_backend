package com.garvey.garveyaicode.rabbitmq.producer;

import com.garvey.garveyaicode.common.config.RabbitMQConfig;
import com.garvey.garveyaicode.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ 生产者（消息发送者）
 */
@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    // 注入Spring提供的RabbitTemplate（核心发送工具）
    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送消息方法
     * @param message 要发送的消息内容
     */
    public boolean sendMessage(String message) {
        // 1. 先确保开启发布确认（全局配置）
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送到交换机确认成功");
            } else {
                System.out.println("消息发送到交换机失败：" + cause);
            }
        });
        // 在 invoke 内部获取当前信道，发送消息
        // 超时时间20ms
        return Boolean.TRUE.equals(rabbitTemplate.invoke(operations -> {
            // 在 invoke 内部获取当前信道，发送消息
            operations.convertAndSend(
                    RabbitMQConfig.TEST_EXCHANGE_NAME,
                    RabbitMQConfig.TEST_ROUTING_KEY,
                    message);
            return operations.waitForConfirms(20L); // 超时时间20ms
        }));
    }

    // 可选：发送对象消息（Spring会自动序列化，推荐用JSON）
    public void sendObjectMessage(User user) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TEST_EXCHANGE_NAME,
                RabbitMQConfig.TEST_ROUTING_KEY,
                user
        );
        System.out.println("生产者发送对象消息成功：" + user);
    }
}
