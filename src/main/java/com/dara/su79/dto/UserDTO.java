package com.dara.su79.dto;

public record UserDTO(
        int id,
        String username,
        String email,
        String firstName,
        String lastName,
        String role,
        StudentDTO student
) {}
