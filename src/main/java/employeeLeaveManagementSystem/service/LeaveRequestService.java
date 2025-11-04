package employeeLeaveManagementSystem.service;

import employeeLeaveManagementSystem.common.LeaveStatus;
import employeeLeaveManagementSystem.common.LeaveType;
import employeeLeaveManagementSystem.dto.LeaveActionDTO;
import employeeLeaveManagementSystem.dto.LeaveRequestDTO;
import employeeLeaveManagementSystem.dto.LeaveResponseDTO;
import employeeLeaveManagementSystem.entity.Employee;
import employeeLeaveManagementSystem.entity.LeaveRequest;
import employeeLeaveManagementSystem.repository.EmployeeDetailsRepo;
import employeeLeaveManagementSystem.repository.LeaveRequestRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepo leaveRequestRepo;
    private final EmployeeDetailsRepo employeeDetailsRepo;
    private final EmployeeService employeeService;


    //Leave Request Submission :
    @Transactional
    public ResponseEntity<String> submitLeave(LeaveRequestDTO dto) {

        Employee employee = employeeDetailsRepo.findById(dto.getEmployeeId()).orElse(null);

        if (employee == null) {
            return new ResponseEntity<>("Invalid employee ID", HttpStatus.BAD_REQUEST);
        }

        List<LeaveRequest> overlaps = leaveRequestRepo.findOverlappingApproved(
                dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate()
        );

        if (!overlaps.isEmpty()) {
            return new ResponseEntity<>("Overlapping approved leave exists", HttpStatus.BAD_REQUEST);
        }

        if (dto.getType() == LeaveType.PRIVILEGED) {
            long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;

            if (employee.getLeaveBalance().compareTo(BigDecimal.valueOf(days)) < 0) {
                return new ResponseEntity<>("Insufficient privileged leave balance", HttpStatus.BAD_REQUEST);
            }
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setType(dto.getType());
        leave.setStatus(LeaveStatus.PENDING);

        leaveRequestRepo.save(leave);

        return ResponseEntity.ok("Leave Request Submitted Successfully");
    }



    //Leave Approval Or Reject :
    @Transactional
    public ResponseEntity<String> leaveApprovalOrReject(LeaveActionDTO dto) {
        LeaveRequest leave = leaveRequestRepo.findById(dto.getLeaveRequestId()).orElse(null);
        if (leave == null) {
            return new ResponseEntity<>("Leave request not found", HttpStatus.BAD_REQUEST);
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            return new ResponseEntity<>("Only pending leaves can be updated", HttpStatus.BAD_REQUEST);
        }

        if (dto.getStatus() == LeaveStatus.APPROVED) {
            if (leave.getType() == LeaveType.PRIVILEGED) {
                long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
                employeeService.adjustBalance(leave.getEmployee().getId(), BigDecimal.valueOf(-days));
            }
            leave.setStatus(LeaveStatus.APPROVED);
            leaveRequestRepo.save(leave);
            return ResponseEntity.ok("Approved Successfully");
        } else {
            leave.setStatus(LeaveStatus.REJECTED);
            leaveRequestRepo.save(leave);

            return ResponseEntity.ok("Rejected Successfully");
        }
    }

}
