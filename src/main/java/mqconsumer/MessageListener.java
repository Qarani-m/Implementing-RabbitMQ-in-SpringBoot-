package mqconsumer;


import mqconsumer.config.MQConfig;
import mqconsumer.entity.CustomMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @RabbitListener(queues = MQConfig.messageQueue)
    public void listenMessageQueue(CustomMessage message) {
        System.out.println("Received message: " + message);
    }

}
