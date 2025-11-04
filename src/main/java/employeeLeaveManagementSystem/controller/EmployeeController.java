package employeeLeaveManagementSystem.controller;

import employeeLeaveManagementSystem.dto.EmployeeRequestDTO;
import employeeLeaveManagementSystem.dto.EmployeeResponseDTO;
import employeeLeaveManagementSystem.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



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



}
