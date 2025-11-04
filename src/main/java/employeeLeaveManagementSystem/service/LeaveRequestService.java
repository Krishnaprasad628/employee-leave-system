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

    @Transactional
    public LeaveResponseDTO submitLeave(LeaveRequestDTO dto) {

        Employee employee = employeeDetailsRepo.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        List<LeaveRequest> overlaps = leaveRequestRepo.findOverlappingApproved(
                dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate());

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("Overlapping approved leave exists");
        }

        if (dto.getType() == LeaveType.PRIVILEGED) {
            long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
            if (employee.getLeaveBalance().compareTo(BigDecimal.valueOf(days)) < 0) {
                throw new IllegalArgumentException("Insufficient privileged leave balance");
            }
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setType(dto.getType());
        leave.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRequestRepo.save(leave);

        return new LeaveResponseDTO(
                saved.getId(),
                saved.getEmployee().getId(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getType(),
                saved.getStatus()
        );
    }


    @Transactional
    public LeaveResponseDTO processLeave(LeaveActionDTO dto) {
        LeaveRequest leave = leaveRequestRepo.findById(dto.getLeaveRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only pending leaves can be updated");
        }

        if (dto.getStatus() == LeaveStatus.APPROVED) {
            if (leave.getType() == LeaveType.PRIVILEGED) {
                long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
                employeeService.adjustBalance(leave.getEmployee().getId(), BigDecimal.valueOf(-days));
            }
            leave.setStatus(LeaveStatus.APPROVED);
        } else {
            leave.setStatus(LeaveStatus.REJECTED);
        }

        LeaveRequest updated = leaveRequestRepo.save(leave);

        return new LeaveResponseDTO(
                updated.getId(),
                updated.getEmployee().getId(),
                updated.getStartDate(),
                updated.getEndDate(),
                LeaveType.valueOf(updated.getType().name()),
                LeaveStatus.valueOf(updated.getStatus().name())
        );
    }
}
