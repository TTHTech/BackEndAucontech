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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserService userService;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authManager;

  public AuthController(UserService s, JwtUtil j, AuthenticationManager m){
    this.userService = s; this.jwtUtil = j; this.authManager = m;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req){
    userService.register(req.username(), req.password());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody AuthRequest req){
    authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
    String token = jwtUtil.generateToken(req.username());
    return new AuthResponse(token, req.username());
  }
}
