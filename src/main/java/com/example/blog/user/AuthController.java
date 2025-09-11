package com.example.blog.user;

import com.example.blog.config.JwtUtil;
import com.example.blog.user.dto.AuthRequest;
import com.example.blog.user.dto.AuthResponse;
import com.example.blog.user.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final UserRepository users; // thêm repo để lấy role

    public AuthController(UserService s, JwtUtil j, AuthenticationManager m, UserRepository users) {
        this.userService = s;
        this.jwtUtil = j;
        this.authManager = m;
        this.users = users;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req.username(), req.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        String token = jwtUtil.generateToken(req.username());
        return new AuthResponse(token, req.username());
    }

    // NEW: trả thông tin user hiện tại để FE phân quyền giao diện
    @GetMapping("/me")
    public UserRes me(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        var u = users.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserRes(u.getId(), u.getUsername(), u.getRole().name());
    }

    // DTO gọn nhẹ trả về cho /me
    record UserRes(Long id, String username, String role) {}
}
