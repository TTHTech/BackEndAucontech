package com.example.blog.admin.dto;

import com.example.blog.user.Role;

public class UserAdminDtos {
    public record CreateUserReq(String username, String password, Role role) {}
    public record UpdateUserReq(String password, Role role) {}
    public record UserRes(Long id, String username, Role role) {}
}
