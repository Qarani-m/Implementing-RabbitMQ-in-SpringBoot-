package mqproducer;

import mqproducer.config.MQConfig;
import mqproducer.entity.CustomMessage;
import mqproducer.entity.DiskInfo;
import mqproducer.entity.OsInfo;
import mqproducer.entity.ServicesInfo;
import mqproducer.service.SystemInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class SpringRabbitmqProducerApplication {


	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	SystemInfo systemInfo;

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

	public static void main(String[] args) {
		SpringApplication.run(SpringRabbitmqProducerApplication.class, args);
	}

	@Bean
	public ScheduledExecutorService scheduledExecutorService() {
		ScheduledExecutorFactoryBean executorFactoryBean = new ScheduledExecutorFactoryBean();
		executorFactoryBean.setPoolSize(1); // Adjust pool size if needed
		executorFactoryBean.setThreadNamePrefix("MessagePublisherThread-");
		executorFactoryBean.setDaemon(true);
		executorFactoryBean.afterPropertiesSet();
		return executorFactoryBean.getObject();
	}

	@Bean
	public Runnable messagePublishingTask() {
		return () -> {
			try {
				publishMessage();
			} catch (Exception e) {
				e.printStackTrace(); // Handle exception appropriately
			}
		};
	}

	@Bean
	public ScheduledFuture<?> scheduleMessagePublishingTask() {
		return scheduledExecutorService().scheduleAtFixedRate(
				messagePublishingTask(),
				0, // Initial delay
				1, // Period (in seconds)
				TimeUnit.SECONDS
		);
	}
}
