package com.edu.module.user.controller;

import com.edu.common.PageResult;
import com.edu.common.Result;
import com.edu.module.user.entity.User;
import com.edu.module.user.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/me")
    public Result<?> currentUser(Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username);
        user.setPassword(null);
        return Result.ok(user);
    }

    @PutMapping("/user/me")
    public Result<?> updateProfile(Authentication auth, @RequestBody User updates) {
        Long userId = (Long) auth.getCredentials();
        userService.updateProfile(userId, updates);
        return Result.ok();
    }

    @PutMapping("/user/me/password")
    public Result<?> changePassword(Authentication auth, @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getCredentials();
        userService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return Result.ok();
    }

    // --- Admin endpoints ---

    @GetMapping("/admin/users")
    public Result<?> listUsers(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String keyword) {
        return Result.ok(PageResult.from(userService.listUsers(page, size, keyword)));
    }

    @PutMapping("/admin/users/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateStatus(id, body.get("status"));
        return Result.ok();
    }
}
