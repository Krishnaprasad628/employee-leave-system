package employeeLeaveManagementSystem.service;

import employeeLeaveManagementSystem.dto.LeaveEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LeaveEventProducerService {

    private final KafkaTemplate<String, LeaveEventDTO> kafkaTemplate;

    @Value("${kafka.topics.leave-events}")
    private String topic;

    public LeaveEventProducerService(KafkaTemplate<String, LeaveEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishLeaveApprovedEvent(LeaveEventDTO event) {
        log.info("Publishing LeaveEvent: {}", event);

        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Leave event published successfully. Offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish leave event", ex);
                    }
                });
    }

}
