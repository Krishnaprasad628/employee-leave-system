package employeeLeaveManagementSystem.repository;

import employeeLeaveManagementSystem.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmployeeDetailsRepo extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);
}
