package com.edu.module.user.controller;

import com.edu.common.Result;
import com.edu.module.user.entity.User;
import com.edu.module.user.service.UserService;
import com.edu.security.JwtTokenProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest req) {
        User user = userService.register(req.getUsername(), req.getPassword(), req.getRole());
        return Result.ok(Map.of("userId", user.getId()));
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest req) {
        User user = userService.findByUsername(req.getUsername());
        if (user == null || user.getStatus() == 0) {
            return Result.fail(401, "用户名不存在或账号已禁用");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return Result.fail(401, "密码错误");
        }
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("realName", user.getRealName());
        data.put("avatar", user.getAvatar());
        return Result.ok(data);
    }

    @PostMapping("/refresh")
    public Result<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return Result.fail(401, "Token无效或已过期");
        }
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String username = jwtTokenProvider.getUsername(refreshToken);
        User user = userService.getById(userId);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, user.getRole());
        return Result.ok(Map.of("accessToken", newAccessToken));
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    static class RegisterRequest {
        private String username;
        private String password;
        private String role;
    }
}
