package employeeLeaveManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveEventDTO {

    private String eventType;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private long totalDays;
    private ZonedDateTime approvedAt;
}
