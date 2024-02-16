package mqproducer.controller;

import mqproducer.config.MQConfig;
import mqproducer.entity.CustomMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/publish")
    public  String publishMessage(@RequestBody CustomMessage customMessage){
         customMessage.setMessage("Hello there");
         rabbitTemplate.convertAndSend(MQConfig.messageTopicExchange, MQConfig.messageRoutingKey, customMessage);
         return "mesasge Published";


    }
}
