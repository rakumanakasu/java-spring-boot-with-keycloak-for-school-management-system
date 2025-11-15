package com.dara.su79.controllers;

import com.dara.su79.dto.StudentDTO;
import com.dara.su79.dto.StudentDetailDTO;
import com.dara.su79.dto.UserProfileDTO;
import com.dara.su79.models.Student;
import com.dara.su79.services.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ------------------ Get all students ------------------
    @GetMapping("")
    public ResponseEntity<Page<StudentDTO>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<StudentDTO> studentDTOs = studentService.getAllStudentsDTO(page, size);
        return ResponseEntity.ok(studentDTOs);
    }

    // ------------------ Create new student ------------------
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createStudent(
            @ModelAttribute Student student,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "classroomId", required = false) Long classroomId
    ) throws Exception {

        Student savedStudent = studentService.createStudent(student, file, classroomId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student created successfully!");
        response.put("data", savedStudent);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ------------------ Get students by class ------------------
    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<StudentDTO>> getStudentsByClass(@PathVariable Long classId) {
        List<Student> students = studentService.findByClassId(classId); // You already have this in AttendanceController
        List<StudentDTO> dtoList = students.stream().map(s -> new StudentDTO(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getGender(),
                s.getDob() != null ? s.getDob().toString() : null,
                s.getAddress(),
                s.getEmail(),
                s.getUser() != null && s.getUser().getUserProfile() != null
                        ? new UserProfileDTO(
                        s.getUser().getUserProfile().getPhone(),
                        s.getUser().getUserProfile().getAddress()
                )
                        : null,
                s.getPhoto()
        )).toList();
        return ResponseEntity.ok(dtoList);
    }



    // ------------------ Get student by ID ------------------test on Postman
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> findById(@PathVariable long id) {
        Student s = studentService.findStudentById(id);

        StudentDTO dto = new StudentDTO(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getGender(),
                s.getDob() != null ? s.getDob().toString() : null,
                s.getAddress(),
                s.getEmail(),
                s.getUser() != null && s.getUser().getUserProfile() != null
                        ? new UserProfileDTO(
                        s.getUser().getUserProfile().getPhone(),
                        s.getUser().getUserProfile().getAddress()
                )
                        : null,
                s.getPhoto()
        );

        return ResponseEntity.ok(dto);
    }

    // ------------------ Search student by email ------------------
    @GetMapping("/searchByEmail")
    public ResponseEntity<StudentDTO> getStudentByEmail(@RequestParam String email) {
        Student student = studentService.findByEmail(email); // You need to implement this
        StudentDTO dto = new StudentDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getGender(),
                student.getDob() != null ? student.getDob().toString() : null,
                student.getAddress(),
                student.getEmail(),
                student.getUser() != null && student.getUser().getUserProfile() != null
                        ? new UserProfileDTO(
                        student.getUser().getUserProfile().getPhone(),
                        student.getUser().getUserProfile().getAddress()
                )
                        : null,
                student.getPhoto()
        );
        return ResponseEntity.ok(dto);
    }


    // ------------------ Get full Student Detail ------------------
    @GetMapping("/detail/{id}")
    public ResponseEntity<StudentDetailDTO> getStudentDetail(@PathVariable long id) {
        StudentDetailDTO studentDetail = studentService.getStudentDetail(id);
        return ResponseEntity.ok(studentDetail);
    }

    // ------------------ Search students by name ------------------
    @GetMapping("/search")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Student> students = studentService.findByName(name, page, size);

        Page<StudentDTO> dtoPage = students.map(s -> new StudentDTO(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getGender(),
                s.getDob() != null ? s.getDob().toString() : null,
                s.getAddress(),
                s.getEmail(),
                s.getUser() != null && s.getUser().getUserProfile() != null
                        ? new UserProfileDTO(
                        s.getUser().getUserProfile().getPhone(),
                        s.getUser().getUserProfile().getAddress()
                )
                        : null,
                s.getPhoto()
        ));

        return ResponseEntity.ok(dtoPage);
    }

    // ------------------ Update student ------------------
    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateStudent(
            @PathVariable long id,
            @ModelAttribute Student student,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "classroomId", required = false) Long classroomId
    ) throws Exception {

        Student updatedStudent = studentService.updateStudent(id, student, file, classroomId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student updated successfully!");
        response.put("data", updatedStudent);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // ------------------ Delete student ------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable long id) {
        studentService.deleteStudent(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Student deleted successfully!");
        return ResponseEntity.ok(response);
    }
}
