package employeeLeaveManagementSystem.controller;

import employeeLeaveManagementSystem.dto.LeaveActionDTO;
import employeeLeaveManagementSystem.dto.LeaveRequestDTO;
import employeeLeaveManagementSystem.service.LeaveRequestService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/leaveRequest")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }


    @PostMapping("/submit")
    public ResponseEntity<String> submitLeave(@RequestBody LeaveRequestDTO dto) {
        return leaveRequestService.submitLeave(dto);
    }


    //Leave Approval Or Reject :
    @PostMapping("/leaveApprovalOrReject")
    public ResponseEntity<String> leaveApprovalOrReject(@RequestBody LeaveActionDTO dto) {
        return leaveRequestService.leaveApprovalOrReject(dto);
    }
}
