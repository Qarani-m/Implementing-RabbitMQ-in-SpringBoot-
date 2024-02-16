package mqproducer.controller;

import mqproducer.config.MQConfig;
import mqproducer.entity.CustomMessage;
import mqproducer.entity.DiskInfo;
import mqproducer.entity.OsInfo;
import mqproducer.entity.ServicesInfo;
import mqproducer.service.SystemInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    SystemInfo systemInfo;

    @GetMapping("/")
    public  String publishMessage() throws Exception {
        DiskInfo diskInfo = DiskInfo.builder()
                .systemRoot(systemInfo.diskInfo().get("systemRoot"))
                .totalSpace(systemInfo.diskInfo().get("totalSpace"))
                .freeSpace( systemInfo.diskInfo().get("freeSpace"))
                .usableSpace(systemInfo.diskInfo().get("usableSpace"))
                .build();
        OsInfo osInfo = OsInfo.builder()
                .os(systemInfo.osInfo().get("os"))
                .osVersion(systemInfo.osInfo().get("osVersion"))
                .osArch(systemInfo.osInfo().get("osArch"))
                .processors(systemInfo.osInfo().get("availableProcessors"))
                .build();
        ServicesInfo servicesInfo = ServicesInfo.builder()
                .services(systemInfo.getServiceNames())
                .cpuLoad(systemInfo.getProcessorCpuLoad())
                .build();

        CustomMessage customMessage = CustomMessage.builder()
                .servicesInfo(servicesInfo)
                .diskInfo(diskInfo)
                .osInfo(osInfo)
                .build();
         rabbitTemplate.convertAndSend(MQConfig.messageTopicExchange, MQConfig.messageRoutingKey, customMessage);
         return "mesasge Published";
    }
}
