package employeeLeaveManagementSystem.service;

import employeeLeaveManagementSystem.dto.EmployeeDTO;
import employeeLeaveManagementSystem.entity.Employee;
import employeeLeaveManagementSystem.repository.EmployeeDetailsRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeDetailsRepo employeeDetailsRepo;
    public static final BigDecimal ACCRUAL_PER_MONTH = BigDecimal.valueOf(1.75);
    public static final BigDecimal MAX_BALANCE = BigDecimal.valueOf(30.0);


    //Save Or Update the Employee Details :
    public ResponseEntity<String> saveOrUpdate(EmployeeDTO dto) {

        Employee employee;
        boolean isUpdate = dto.getId() != null;

        if (isUpdate) {
            employee = employeeDetailsRepo.findById(dto.getId()).orElse(null);

            if (employee == null) {
                return new ResponseEntity<>("Employee not found with id: " + dto.getId(), HttpStatus.BAD_REQUEST);
            }

            if (!dto.getEmail().equalsIgnoreCase(employee.getEmail()) && employeeDetailsRepo.existsByEmail(dto.getEmail())) {
                return new ResponseEntity<>("Email already exists for another employee", HttpStatus.BAD_REQUEST);
            }

            employee.setName(dto.getName());
            employee.setEmail(dto.getEmail());
            employee.setJoiningDate(dto.getJoiningDate());

        } else {
            if (employeeDetailsRepo.existsByEmail(dto.getEmail())) {
                return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
            }

            employee = new Employee();
            employee.setName(dto.getName());
            employee.setEmail(dto.getEmail());
            employee.setJoiningDate(dto.getJoiningDate());
        }

        BigDecimal accrued = calculateAccrued(dto.getJoiningDate(), LocalDate.now());
        employee.setLeaveBalance(accrued.min(MAX_BALANCE));

        employeeDetailsRepo.save(employee);

        String message = isUpdate
                ? "Employee Details Updated Successfully"
                : "Employee Details Created Successfully";

        return ResponseEntity.ok(message);
    }

    public BigDecimal calculateAccrued(LocalDate joiningDate, LocalDate asOf) {
        if (joiningDate == null || asOf == null || joiningDate.isAfter(asOf)) {
            return BigDecimal.ZERO;
        }

        long fullMonths = ChronoUnit.MONTHS.between(joiningDate.withDayOfMonth(1),
                asOf.withDayOfMonth(1)
        );

        BigDecimal accrual = ACCRUAL_PER_MONTH.multiply(BigDecimal.valueOf(fullMonths));

        if (accrual.compareTo(MAX_BALANCE) > 0) {
            accrual = MAX_BALANCE;
        }

        return accrual.setScale(2, RoundingMode.HALF_UP);
    }


    @Transactional
    public void adjustBalance(Long employeeId, BigDecimal delta) {
        Employee e = employeeDetailsRepo.findById(employeeId).orElse(null);
        if (e == null) {
            return;
        }
        BigDecimal newBalance = e.getLeaveBalance().add(delta);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            return;
        }
        if (newBalance.compareTo(MAX_BALANCE) > 0) {
            newBalance = MAX_BALANCE;
        }
        e.setLeaveBalance(newBalance);
        employeeDetailsRepo.save(e);
    }




    public ResponseEntity<?> getLeaveBalance(Long employeeId) {
        Employee employee = employeeDetailsRepo.findById(employeeId).orElse(null);
        if (employee == null) {
            return new ResponseEntity<>("Employee not found with id: " + employeeId, HttpStatus.BAD_REQUEST);
        }

        EmployeeDTO response = new EmployeeDTO();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setJoiningDate(employee.getJoiningDate());
        response.setLeaveBalance(employee.getLeaveBalance());

        return ResponseEntity.ok(response);
    }

}
