package employeeLeaveManagementSystem.dto;

import employeeLeaveManagementSystem.common.LeaveStatus;
import employeeLeaveManagementSystem.common.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveResponseDTO {

    private Long id;
    private Long employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType type;
    private LeaveStatus status;

}
