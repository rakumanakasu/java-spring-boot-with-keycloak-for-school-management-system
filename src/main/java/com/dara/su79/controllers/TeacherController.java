package com.dara.su79.controllers;

import com.dara.su79.dto.TeacherDTO;
import com.dara.su79.models.Subject;
import com.dara.su79.models.Teacher;
import com.dara.su79.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    @Autowired
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("")
    public ResponseEntity<Page<TeacherDTO>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(teacherService.getAllTeachers(page, size), HttpStatus.OK);
    }


    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createTeacher(
            @ModelAttribute Teacher teacher,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {

        Teacher savedTeacher = teacherService.createTeacher(teacher, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Teacher created successfully");
        response.put("data", savedTeacher);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> findById(@PathVariable long id) {
        return new ResponseEntity<>(teacherService.findTeacherById(id), HttpStatus.OK);
    }



    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateTeacher(
            @PathVariable long id,
            @ModelAttribute Teacher teacher,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        Teacher updatedTeacher = teacherService.updateById(id, teacher, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Teacher updated successfully!");
        response.put("data", updatedTeacher);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/searchByName")
    public ResponseEntity<Page<TeacherDTO>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TeacherDTO> teachers = teacherService.findByName(name, page, size);
        return new ResponseEntity<>(teachers, HttpStatus.OK);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTeacher(@PathVariable long id) {
        teacherService.deleteTeacher(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Teacher deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
