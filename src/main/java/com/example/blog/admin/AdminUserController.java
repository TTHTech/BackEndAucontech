package com.example.blog.admin;

import com.example.blog.admin.dto.UserAdminDtos.CreateUserReq;
import com.example.blog.admin.dto.UserAdminDtos.UpdateUserReq;
import com.example.blog.admin.dto.UserAdminDtos.UserRes;
import com.example.blog.user.Role;
import com.example.blog.user.UserEntity;
import com.example.blog.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AdminUserController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    // LIST (phân trang + search theo username)
    @GetMapping
    public Page<UserRes> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String q
    ) {
        var pageable = PageRequest.of(page, size);
        Page<UserEntity> res = (q == null || q.isBlank())
                ? users.findAll(pageable)
                : users.findByUsernameContainingIgnoreCase(q.trim(), pageable);
        return res.map(u -> new UserRes(u.getId(), u.getUsername(), u.getRole()));
    }

    // GET detail
    @GetMapping("/{id}")
    public UserRes detail(@PathVariable Long id) {
        UserEntity u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserRes(u.getId(), u.getUsername(), u.getRole());
    }

    // CREATE user (ADMIN tạo user mới, chọn role)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRes create(@RequestBody CreateUserReq req) {
        if (req == null || req.username() == null || req.username().isBlank() || req.password() == null || req.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username/password required");
        }
        if (users.findByUsername(req.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        UserEntity u = new UserEntity();
        u.setUsername(req.username().trim());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(req.role() != null ? req.role() : Role.ROLE_USER);
        u = users.save(u);
        return new UserRes(u.getId(), u.getUsername(), u.getRole());
    }

    // UPDATE user (đổi role hoặc password)
    @PutMapping("/{id}")
    public UserRes update(@PathVariable Long id, @RequestBody UpdateUserReq req) {
        UserEntity u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (req != null) {
            if (req.password() != null && !req.password().isBlank()) {
                u.setPasswordHash(encoder.encode(req.password()));
            }
            if (req.role() != null) {
                u.setRole(req.role());
            }
        }
        u = users.save(u);
        return new UserRes(u.getId(), u.getUsername(), u.getRole());
    }

    // DELETE user
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        // Không cho xóa chính admin mặc định nếu bạn muốn (tuỳ chọn):
        // var me = ... (nếu cần ngăn tự xoá chính mình)
        if (!users.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        users.deleteById(id);
    }
}
