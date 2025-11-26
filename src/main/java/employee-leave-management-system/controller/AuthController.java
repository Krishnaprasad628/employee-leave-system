package employeeLeaveManagementSystem.controller;

import employeeLeaveManagementSystem.config.JwtUtil;
import employeeLeaveManagementSystem.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        UserService.TestUser user = userService.getUsers().get(username);
        if (user == null || !new BCryptPasswordEncoder().matches(password, user.password())) {
            return Map.of("error", "Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.id(), user.role());
        return Map.of("token", token, "userId", user.id().toString(), "role", user.role());
    }
}