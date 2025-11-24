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

    public void sendLeaveApprovedEvent(LeaveEventDTO event) {
        log.info("Sending LeaveEvent: {}", event);

        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Leave event sent successfully. Offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send leave event", ex);
                    }
                });
    }

}
