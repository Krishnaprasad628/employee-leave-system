package employeeLeaveManagementSystem.dto;


import employeeLeaveManagementSystem.common.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveActionDTO {

    private Long leaveRequestId;
    private LeaveStatus status;

}
