package employeeLeaveManagementSystem.service;

import employeeLeaveManagementSystem.dto.EmployeeDTO;
import employeeLeaveManagementSystem.entity.Employee;
import employeeLeaveManagementSystem.repository.EmployeeDetailsRepo;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class EmployeeService {

    private final EmployeeDetailsRepo employeeDetailsRepo;

    public EmployeeService(EmployeeDetailsRepo employeeDetailsRepo) {
        this.employeeDetailsRepo = employeeDetailsRepo;
    }

    public static final BigDecimal ACCRUAL_PER_MONTH = BigDecimal.valueOf(1.75);
    public static final BigDecimal MAX_BALANCE = BigDecimal.valueOf(30.0);


    //Save Or Update the Employee Details :
    public ResponseEntity<String> saveOrUpdate(EmployeeDTO request) {
        String response;
        try {
            boolean isUpdate = request.getId() != null;

            Employee employee = isUpdate ? fetchEmployeeDetails(request.getId()) : new Employee();

            if (emailExists(request.getEmail(), employee)) {
                return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
            }

            updateEmployeeDetails(employee, request);
            employee.setLeaveBalance(calculateLeave(request.getJoiningDate()));

            employeeDetailsRepo.save(employee);

            response = isUpdate ? "Employee Updated Successfully" : "Employee Saved Successfully";

            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            log.error("Error saving or updating employee: {}", ex.getMessage(), ex);
            return new ResponseEntity<>("Failed to Save or Update employee: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    private Employee fetchEmployeeDetails(Long id) {
        return employeeDetailsRepo.findById(id).orElseThrow(() -> new RuntimeException("Employee Id is not found" + id));
    }


    private boolean emailExists(String email, Employee employee) {
        if (employee.getId() == null) {
            return employeeDetailsRepo.existsByEmail(email);
        } else {
            return !email.equalsIgnoreCase(employee.getEmail()) && employeeDetailsRepo.existsByEmail(email);
        }
    }


    private void updateEmployeeDetails(Employee employee, EmployeeDTO request) {
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setJoiningDate(request.getJoiningDate());
    }

    private BigDecimal calculateLeave(LocalDate joinedDate) {
        BigDecimal accrued = calculateAccrued(joinedDate, LocalDate.now());
        return accrued.min(MAX_BALANCE);
    }


    private BigDecimal calculateAccrued(LocalDate joinedDate, LocalDate currentDate) {
        long monthWorked = ChronoUnit.MONTHS.between(joinedDate.withDayOfMonth(1), currentDate.withDayOfMonth(1));
        BigDecimal accrualPerMonth = new BigDecimal(String.valueOf(ACCRUAL_PER_MONTH));
        return accrualPerMonth.multiply(BigDecimal.valueOf(monthWorked));
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


    //To List the leave balance :
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


    //Carry forward the balance leaves :
    @Transactional
    public void carryForwardFinancialYearLeave() {
        List<Employee> employees = employeeDetailsRepo.findAll();

        for (Employee employee : employees) {
            BigDecimal currentBalance = employee.getLeaveBalance() != null
                    ? employee.getLeaveBalance()
                    : BigDecimal.ZERO;

            BigDecimal carryForward = currentBalance.min(BigDecimal.TEN);
            employee.setLeaveBalance(carryForward);
        }

        employeeDetailsRepo.saveAll(employees);
    }


    // Runs automatically every April 1st at midnight
    @Scheduled(cron = "0 0 0 1 4 *")
    public void carryForwardYearlyLeaves() {
        this.carryForwardFinancialYearLeave();
    }



    @Cacheable(value = "balance", key = "#employeeId")
    public EmployeeDTO getCachedBalance(Long employeeId) {
        log.info("CACHE MISS → Fetching from DB for employeeId={}", employeeId);

        Employee employee = employeeDetailsRepo.findById(employeeId).orElse(null);
        if (employee == null) {
            return null;
        }

        EmployeeDTO response = new EmployeeDTO();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setJoiningDate(employee.getJoiningDate());
        response.setLeaveBalance(employee.getLeaveBalance());

        return response;
    }


}
