package employeeLeaveManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EmployeeLeaveManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeLeaveManagementSystemApplication.class, args);
    }
}
