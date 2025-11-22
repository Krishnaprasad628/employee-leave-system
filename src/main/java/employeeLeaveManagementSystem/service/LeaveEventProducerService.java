package employeeLeaveManagementSystem.service;

import employeeLeaveManagementSystem.dto.LeaveEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LeaveEventProducerService {

    private final KafkaTemplate<String, LeaveEventDTO> kafkaTemplate;

    public LeaveEventProducerService(KafkaTemplate<String, LeaveEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String TOPIC = "leave-events";

    public void publishLeaveApprovedEvent(LeaveEventDTO event) {
        log.info("Publishing LeaveEvent to Kafka: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }


}
