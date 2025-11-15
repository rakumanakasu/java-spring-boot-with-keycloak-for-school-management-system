package com.dara.su79.controllers;

import com.dara.su79.dto.GradeDTO;
import com.dara.su79.dto.StudentDTO;
import com.dara.su79.models.Grade;
import com.dara.su79.services.GradeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }


    // ------------------- Get all grades paged as DTO -------------------
    @GetMapping("")
    public ResponseEntity<Page<GradeDTO>> getAllGrades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<GradeDTO> dtoPage = gradeService.getAllGradesDTO(page, size);
        return ResponseEntity.ok(dtoPage);
    }
    /**
     * Create new grade
     */
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createGrade(@RequestBody Grade grade) {
        Grade saved = gradeService.createGrade(grade);
        GradeDTO dto = gradeService.convertToDTO(saved);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Grade created successfully");
        response.put("data", dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get grade by ID (as DTO)
     */
    @GetMapping("/{id}")
    public ResponseEntity<GradeDTO> findById(@PathVariable Long id) {
        Grade grade = gradeService.findById(id);
        return ResponseEntity.ok(gradeService.convertToDTO(grade));
    }

    /**
     * Update grade
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateGrade(@PathVariable Long id, @RequestBody Grade grade) {
        Grade updated = gradeService.updateGrade(id, grade);
        GradeDTO dto = gradeService.convertToDTO(updated);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Grade updated successfully");
        response.put("data", dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete grade
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Grade deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get all grades by student
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<GradeDTO> getGradeByStudent(@PathVariable Long studentId) {
        List<GradeDTO> grades = gradeService.findByStudent(studentId);
        if (grades.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // return the first grade as a single object
        return ResponseEntity.ok(grades.get(0));
    }

    /**
     * Get all grades by subject
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<GradeDTO> getGradeBySubject(@PathVariable Long subjectId) {
        List<GradeDTO> grades = gradeService.findBySubject(subjectId);
        if (grades.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(grades.get(0));
    }

    /**
     * Get all grades by student & subject
     */
    @GetMapping("/student/{studentId}/subject/{subjectId}")
    public ResponseEntity<GradeDTO> getGradeByStudentAndSubject(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        List<GradeDTO> grades = gradeService.findByStudentAndSubject(studentId, subjectId);
        if (grades.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(grades.get(0));
    }
}
