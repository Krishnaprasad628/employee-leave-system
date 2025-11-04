package employeeLeaveManagementSystem.service;

import employeeLeaveManagementSystem.dto.EmployeeRequestDTO;
import employeeLeaveManagementSystem.dto.EmployeeResponseDTO;
import employeeLeaveManagementSystem.entity.Employee;
import employeeLeaveManagementSystem.repository.EmployeeDetailsRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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


    @Transactional
    public EmployeeResponseDTO saveOrUpdate(EmployeeRequestDTO dto) {
        Employee employee;

        if (dto.getId() != null) {
            employee = employeeDetailsRepo.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + dto.getId()));

            if (!dto.getEmail().equalsIgnoreCase(employee.getEmail())
                    && employeeDetailsRepo.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists for another employee");
            }

            employee.setName(dto.getName());
            employee.setEmail(dto.getEmail());
            employee.setJoiningDate(dto.getJoiningDate());

        } else {
            if (employeeDetailsRepo.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }

            employee = new Employee();
            employee.setName(dto.getName());
            employee.setEmail(dto.getEmail());
            employee.setJoiningDate(dto.getJoiningDate());
        }

        BigDecimal accrued = calculateAccrued(dto.getJoiningDate(), LocalDate.now());
        employee.setLeaveBalance(accrued.min(MAX_BALANCE));

        Employee saved = employeeDetailsRepo.save(employee);
        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setEmail(saved.getEmail());
        response.setJoiningDate(saved.getJoiningDate());
        response.setLeaveBalance(saved.getLeaveBalance());

        return response;
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
    public Employee adjustBalance(Long employeeId, BigDecimal delta) {
        Employee e = employeeDetailsRepo.findById(employeeId).orElseThrow();
        BigDecimal newBalance = e.getLeaveBalance().add(delta);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance would go negative");
        }
        if (newBalance.compareTo(MAX_BALANCE) > 0) newBalance = MAX_BALANCE;
        e.setLeaveBalance(newBalance);
        return employeeDetailsRepo.save(e);
    }




    public EmployeeResponseDTO getLeaveBalance(Long employeeId) {
        Employee employee = employeeDetailsRepo.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + employeeId));

        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setLeaveBalance(employee.getLeaveBalance());

        return response;
    }

}
