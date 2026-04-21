package com.garvey.garveyaicode.rabbitmq;

import com.garvey.garveyaicode.model.entity.User;
import com.garvey.garveyaicode.rabbitmq.producer.RabbitMQProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @Test
    public void testSendMessage() {
        // 发送测试消息
        rabbitMQProducer.sendMessage("Hello Spring + RabbitMQ!");

        // 可选：发送对象消息
/*        User user = new User();
        user.setUserName("张三");
        user.setUserAccount("zhangsan");
        rabbitMQProducer.sendObjectMessage(user);*/

        // 休眠1秒，确保消费者接收到消息（测试用）
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
