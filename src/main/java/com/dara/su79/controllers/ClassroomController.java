package com.dara.su79.controllers;


import com.dara.su79.dto.ClassroomDTO;
import com.dara.su79.dto.ClassroomRequestDTO;
import com.dara.su79.models.Classroom;
import com.dara.su79.services.ClassroomService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllClassrooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Classroom> classroomsPage = classroomService.getAllClassrooms(page, size);

        List<ClassroomDTO> classroomDTOs = classroomsPage.getContent().stream()
                .map(classroomService::convertToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", classroomDTOs);
        response.put("totalElements", classroomsPage.getTotalElements());
        response.put("totalPages", classroomsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createClassroom(@RequestBody ClassroomRequestDTO dto) {
        Classroom saved = classroomService.createClassroom(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Classroom created successfully");
        response.put("data", classroomService.convertToDTO(saved));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> findById(@PathVariable Long id) {
        Classroom classroom = classroomService.findById(id);
        return ResponseEntity.ok(classroomService.convertToDTO(classroom));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClassroom(@PathVariable Long id,
                                                               @RequestBody ClassroomRequestDTO dto) {
        Classroom updated = classroomService.updateClassroom(id, dto);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Classroom updated successfully");
        response.put("data", classroomService.convertToDTO(updated));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Classroom deleted successfully");
        return ResponseEntity.ok(response);
    }
}
