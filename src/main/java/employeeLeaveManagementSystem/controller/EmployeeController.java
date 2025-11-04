package employeeLeaveManagementSystem.controller;

import employeeLeaveManagementSystem.dto.EmployeeRequestDTO;
import employeeLeaveManagementSystem.dto.EmployeeResponseDTO;
import employeeLeaveManagementSystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employeeDetails")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/save")
    public ResponseEntity<EmployeeResponseDTO> saveOrUpdate(@RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO responseDTO = employeeService.saveOrUpdate(dto);
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/leaveBalanceCheck/{id}")
    public ResponseEntity<EmployeeResponseDTO> getLeaveBalance(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getLeaveBalance(id));
    }


}
