package com.dara.su79.controllers;

import com.dara.su79.dto.RegisterRequest;
import com.dara.su79.models.User;
import com.dara.su79.services.UserService;
import com.dara.su79.services.KeycloakAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService; // Inject UserService
    private final KeycloakAdminService keycloakService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Call UserService to register user and assign default role
            User user = userService.registerStudent(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("username", user.getUsername());
            response.put("role", user.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestParam String username) {
//        keycloakService.sendResetPasswordEmail(username);
//        return ResponseEntity.ok("Password reset email sent if user exists");
//    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        keycloakService.sendResetPasswordEmailByEmail(email);
        return ResponseEntity.ok("Password reset email sent if user exists");
    }


}
