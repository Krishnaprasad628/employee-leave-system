package employeeLeaveManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "employeeLeaveManagementSystem"
})
@EnableJpaRepositories(basePackages = "employeeLeaveManagementSystem.repository")
@EntityScan(basePackages = "employeeLeaveManagementSystem.entity")
public class EmployeeLeaveManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeLeaveManagementSystemApplication.class, args);
    }
}
