package employeeLeaveManagementSystem.controller;

import employeeLeaveManagementSystem.dto.EmployeeDTO;
import employeeLeaveManagementSystem.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/employeeDetails")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }


    //Save Or Update the Employee Details :
    @PostMapping("/saveOrUpdate")
    public ResponseEntity<String> saveOrUpdate(@RequestBody EmployeeDTO dto) {
        return employeeService.saveOrUpdate(dto);
    }


    //To List the leave balance :
    @GetMapping("/leaveBalanceCheck/{id}")
    public ResponseEntity<?> getLeaveBalance(@PathVariable Long id) {
        return employeeService.getLeaveBalance(id);
    }


}
