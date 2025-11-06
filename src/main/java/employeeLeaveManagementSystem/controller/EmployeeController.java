package employeeLeaveManagementSystem.controller;

import employeeLeaveManagementSystem.dto.EmployeeRequestDTO;
import employeeLeaveManagementSystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employeeDetails")
public class EmployeeController {

    private final EmployeeService employeeService;


    //Save Or Update the Employee Details :
    @PostMapping("/saveOrUpdate")
    public ResponseEntity<String> saveOrUpdate(@RequestBody EmployeeRequestDTO dto) {
        return employeeService.saveOrUpdate(dto);
    }


    @GetMapping("/leaveBalanceCheck/{id}")
    public ResponseEntity<?> getLeaveBalance(@PathVariable Long id) {
        return employeeService.getLeaveBalance(id);
    }


}
