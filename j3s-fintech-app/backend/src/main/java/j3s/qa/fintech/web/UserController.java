package j3s.qa.fintech.web;

import j3s.qa.fintech.dto.ApiResponse;
import j3s.qa.fintech.dto.CreateUserRequest;
import j3s.qa.fintech.model.User;
import j3s.qa.fintech.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@Valid @RequestBody CreateUserRequest req) {
        User saved = userService.create(req.getName(), req.getEmail(), req.getAccountType());
        return ResponseEntity.ok(ApiResponse.ok("User created", saved));
    }

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userService.all());
    }
}