package employeeLeaveManagementSystem.controller;

import employeeLeaveManagementSystem.dto.LeaveActionDTO;
import employeeLeaveManagementSystem.dto.LeaveRequestDTO;
import employeeLeaveManagementSystem.dto.LeaveResponseDTO;
import employeeLeaveManagementSystem.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/leaveRequest")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;


    @PostMapping("/submit")
    public ResponseEntity<LeaveResponseDTO> submitLeave(@RequestBody LeaveRequestDTO dto) {
        return ResponseEntity.ok(leaveRequestService.submitLeave(dto));
    }

    @PostMapping("/process")
    public ResponseEntity<LeaveResponseDTO> processLeave(@RequestBody LeaveActionDTO dto) {
        return ResponseEntity.ok(leaveRequestService.processLeave(dto));
    }
}
