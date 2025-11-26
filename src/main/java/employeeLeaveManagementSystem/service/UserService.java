package employeeLeaveManagementSystem.service;

import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Getter
public class UserService {

    private final Map<String, TestUser> users;

    public UserService() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        users = Map.of(
                "emp1", new TestUser(1L, "emp1", encoder.encode("password"), "ROLE_EMPLOYEE"),
                "mgr1", new TestUser(2L, "mgr1", encoder.encode("password"), "ROLE_MANAGER")
        );
    }

    public record TestUser(Long id, String username, String password, String role) {
    }
}