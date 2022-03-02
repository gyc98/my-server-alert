package cn.billycloud.myserveralert.rabbit;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${my.rabbitmq.queue.name}")
    private String queueName;

    public void send(String context) {
        this.rabbitTemplate.convertAndSend(queueName, context);
    }

}
