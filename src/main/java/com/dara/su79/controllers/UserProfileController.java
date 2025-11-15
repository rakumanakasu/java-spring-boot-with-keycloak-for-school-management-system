package com.dara.su79.controllers;

import com.dara.su79.models.UserProfile;
import com.dara.su79.services.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/userprofiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        return ResponseEntity.ok(userProfileService.getAllProfiles());
    }

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createProfile(@RequestBody UserProfile profile) {
        UserProfile saved = userProfileService.createProfile(profile);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile created successfully");
        response.put("data", saved);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> findById(@PathVariable int id) {
        return ResponseEntity.ok(userProfileService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProfile(@PathVariable int id, @RequestBody UserProfile profile) {
        UserProfile updated = userProfileService.updateProfile(id, profile);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable int id) {
        userProfileService.deleteProfile(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile deleted successfully");
        return ResponseEntity.ok(response);
    }
}
