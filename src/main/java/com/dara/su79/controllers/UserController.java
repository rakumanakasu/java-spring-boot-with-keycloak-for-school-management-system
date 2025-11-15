package com.dara.su79.controllers;

import com.dara.su79.dto.StudentDTO;
import com.dara.su79.dto.UserDTO;
import com.dara.su79.dto.UserProfileDTO;
import com.dara.su79.models.User;
import com.dara.su79.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = {"application/json", "application/json;charset=UTF-8"})
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            userService.createStudentUser(user);
        } else if ("TEACHER".equalsIgnoreCase(user.getRole())) {
            userService.createTeacherUser(user);
        } else {
            userService.createUser(user);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "User created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }




    @GetMapping(value = {"", "/"})
    public ResponseEntity<?> getUsersAll() {
        var users = userService.getUserAll();

        var dtos = users.stream()
                .map(u -> {
                    StudentDTO studentDTO = null;

                    if (u.getStudent() != null) {
                        UserProfileDTO userProfileDTO = null;

                        if (u.getUserProfile() != null) {
                            userProfileDTO = new UserProfileDTO(
                                    u.getUserProfile().getPhone(),
                                    u.getUserProfile().getAddress()
                            );
                        }

                        studentDTO = new StudentDTO(
                                u.getStudent().getId(),
                                u.getStudent().getFirstName(),
                                u.getStudent().getLastName(),
                                u.getStudent().getGender(),
                                u.getStudent().getDob() != null ? u.getStudent().getDob().toString() : null,
                                u.getStudent().getAddress(),
                                u.getStudent().getEmail(),
                                userProfileDTO,
                                u.getStudent().getPhoto()
                        );
                    }

                    return new UserDTO(
                            u.getId(),
                            u.getUsername(),
                            u.getEmail(),
                            u.getFirstName(),
                            u.getLastName(),
                            u.getRole(),
                            studentDTO
                    );
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody UserDTO userDto) {
        // 1. Find existing user
        User user = userService.findById(id);

        // 2. Update basic User fields
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());
        user.setUsername(userDto.username());

        // 3. Update Student info if present
        if (user.getStudent() != null && userDto.student() != null) {
            StudentDTO studentDto = userDto.student();
            user.getStudent().setFirstName(studentDto.getFirstName());
            user.getStudent().setLastName(studentDto.getLastName());
            user.getStudent().setEmail(studentDto.getEmail());
            user.getStudent().setAddress(studentDto.getAddress());
            user.getStudent().setGender(studentDto.getGender());

            // --- Convert DOB string to LocalDate safely ---
            String dobStr = studentDto.getDob();
            if (dobStr != null && !dobStr.isBlank()) {
                try {
                    LocalDate dob = LocalDate.parse(dobStr);
                    user.getStudent().setDob(dob);
                } catch (DateTimeParseException e) {
                    // log the issue or handle as needed
                    System.out.println("Invalid DOB format: " + dobStr);
                }
            }
        }

        // 4. Save updated user
        userService.saveUser(user); // <-- make sure you have this method in UserService

        // 5. Return success response
        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam("username") String username) {
        var users = userService.searchUsersByUsername(username);

        var dtos = users.stream()
                .map(u -> {
                    StudentDTO studentDTO = null;
                    if (u.getStudent() != null) {
                        UserProfileDTO userProfileDTO = null;
                        if (u.getUserProfile() != null) {
                            userProfileDTO = new UserProfileDTO(
                                    u.getUserProfile().getPhone(),
                                    u.getUserProfile().getAddress()
                            );
                        }
                        studentDTO = new StudentDTO(
                                u.getStudent().getId(),
                                u.getStudent().getFirstName(),
                                u.getStudent().getLastName(),
                                u.getStudent().getGender(),
                                u.getStudent().getDob() != null ? u.getStudent().getDob().toString() : null,
                                u.getStudent().getAddress(),
                                u.getStudent().getEmail(),
                                userProfileDTO,
                                u.getStudent().getPhoto()
                        );
                    }
                    return new UserDTO(
                            u.getId(),
                            u.getUsername(),
                            u.getEmail(),
                            u.getFirstName(),
                            u.getLastName(),
                            u.getRole(),
                            studentDTO
                    );
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<UserDTO>> getUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username) {

        Page<User> usersPage = userService.getUsersPaged(page, size, username);

        Page<UserDTO> dtoPage = usersPage.map(u -> {
            StudentDTO studentDTO = null;
            if (u.getStudent() != null) {
                UserProfileDTO userProfileDTO = null;
                if (u.getUserProfile() != null) {
                    userProfileDTO = new UserProfileDTO(
                            u.getUserProfile().getPhone(),
                            u.getUserProfile().getAddress()
                    );
                }
                studentDTO = new StudentDTO(
                        u.getStudent().getId(),
                        u.getStudent().getFirstName(),
                        u.getStudent().getLastName(),
                        u.getStudent().getGender(),
                        u.getStudent().getDob() != null ? u.getStudent().getDob().toString() : null,
                        u.getStudent().getAddress(),
                        u.getStudent().getEmail(),
                        userProfileDTO,
                        u.getStudent().getPhoto()
                );
            }
            return new UserDTO(
                    u.getId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.getRole(),
                    studentDTO
            );
        });

        return ResponseEntity.ok(dtoPage);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/id")
    public ResponseEntity<UserDTO> getUserById(@RequestParam int id) {
        User user = userService.findById(id);

        // Map UserProfile from User, not from Student
        UserProfileDTO userProfileDTO = null;
        if (user.getUserProfile() != null) {
            userProfileDTO = new UserProfileDTO(
                    user.getUserProfile().getPhone(),
                    user.getUserProfile().getAddress()
            );
        }

        // Map Student if exists
        StudentDTO studentDTO = null;
        if (user.getStudent() != null) {
            studentDTO = new StudentDTO(
                    user.getStudent().getId(),
                    user.getStudent().getFirstName(),
                    user.getStudent().getLastName(),
                    user.getStudent().getGender(),
                    user.getStudent().getDob() != null ? user.getStudent().getDob().toString() : null,
                    user.getStudent().getAddress(),
                    user.getStudent().getEmail(),
                    userProfileDTO, // <-- use UserProfile from User
                    user.getStudent().getPhoto()
            );
        }

        UserDTO dto = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                studentDTO
        );

        return ResponseEntity.ok(dto);
    }


}
