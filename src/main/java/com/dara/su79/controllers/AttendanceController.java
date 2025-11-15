package com.dara.su79.controllers;

import com.dara.su79.dto.AttendanceCreateDTO;
import com.dara.su79.dto.AttendanceInfoDTO;
import com.dara.su79.dto.AttendanceUpdateDTO;
import com.dara.su79.dto.StudentAttendanceDTO;
import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Attendance;
import com.dara.su79.models.Student;
import com.dara.su79.models.Subject;
import com.dara.su79.services.AttendanceService;
import com.dara.su79.services.StudentService;
import com.dara.su79.services.SubjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final StudentService studentService;
    private final SubjectService subjectService;

    public AttendanceController(AttendanceService attendanceService,
                                StudentService studentService,
                                SubjectService subjectService) {
        this.attendanceService = attendanceService;
        this.studentService = studentService;
        this.subjectService = subjectService;
    }


    // ------------------ Get all attendances paginated ------------------
    @GetMapping("")
    public ResponseEntity<List<StudentAttendanceDTO>> getAllAttendances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Student> studentsPage = studentService.getAllStudents(page, size);
        if (studentsPage.isEmpty()) return ResponseEntity.ok(Collections.emptyList());

        List<StudentAttendanceDTO> studentDTOs = studentsPage.getContent().stream()
                .map(student -> {
                    List<Attendance> attendances = student.getAttendances();
                    return mapStudentAttendance(student, attendances == null ? List.of() : attendances);
                })
                .toList();

        return ResponseEntity.ok(studentDTOs);
    }


    // ------------------ Create attendance ------------------
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createAttendance(@RequestBody AttendanceCreateDTO dto) {

        Student student = studentService.findStudentById(dto.getStudentId());
        if (student == null) {
            throw new MyResourceNotFoundException("Student not found");
        }

        Subject subject = subjectService.findSubjectById(dto.getSubjectId());
        if (subject == null) {
            throw new MyResourceNotFoundException("Subject not found");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSubject(subject); // IMPORTANT
        attendance.setPresent(dto.isPresent());
        attendance.setDate(dto.getDate());

        Attendance saved = attendanceService.createAttendanceIfNotExists(attendance);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Attendance recorded successfully");
        response.put("data", mapStudentAttendance(student, List.of(saved)));

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    // ------------------ Get attendance by ID ------------------
    @GetMapping("/{id}")
    public ResponseEntity<StudentAttendanceDTO> findById(@PathVariable Long id) {
        Attendance attendance = attendanceService.findById(id);
        StudentAttendanceDTO dto = mapStudentAttendance(attendance.getStudent(), List.of(attendance));
        return ResponseEntity.ok(dto);
    }

    // ------------------ Update attendance ------------------
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAttendance(
            @PathVariable Long id,
            @RequestBody AttendanceUpdateDTO dto) {

        Attendance updated = attendanceService.updateAttendancePresent(id, dto.getPresent());

        AttendanceInfoDTO updatedDTO = new AttendanceInfoDTO(
                updated.getId(),
                updated.getDate(),
                updated.isPresent(),
                updated.getSubject() != null ? updated.getSubject().getSubjectName() : null,
                updated.getStudent().getClassroom() != null ? updated.getStudent().getClassroom().getClassName() : null,
                updated.getStudent().getClassroom() != null && updated.getStudent().getClassroom().getTeacher() != null
                        ? updated.getStudent().getClassroom().getTeacher().getFirstName() + " " + updated.getStudent().getClassroom().getTeacher().getLastName()
                        : null
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Attendance updated successfully");
        response.put("data", updatedDTO);

        return ResponseEntity.ok(response);
    }

    // ------------------ Delete attendance ------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Attendance deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ------------------ Get attendances by student ------------------
    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentAttendanceDTO> getStudentByAttendance(@PathVariable Long studentId) {
        Student student = studentService.findStudentById(studentId);
        List<Attendance> attendances = attendanceService.findByStudent(studentId);

        return ResponseEntity.ok(mapStudentAttendance(student, attendances));
    }

    // ------------------ Get attendances by class with pagination and search ------------------
    @GetMapping("/class/{classId}")
    public ResponseEntity<Map<String, Object>> getAttendancesByClass(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Student> studentsPage;

        if (search != null && !search.isBlank()) {
            // Search students by firstName or lastName in this class
            studentsPage = studentService.searchStudentsByClassAndName(classId, search, pageRequest);
        } else {
            studentsPage = studentService.getStudentsByClassId(classId, pageRequest);
        }

        List<StudentAttendanceDTO> studentDTOs = studentsPage.getContent().stream()
                .map(student -> {
                    List<Attendance> attendances = student.getAttendances();
                    return mapStudentAttendance(student, attendances == null ? List.of() : attendances);
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("students", studentDTOs);
        response.put("currentPage", studentsPage.getNumber());
        response.put("totalItems", studentsPage.getTotalElements());
        response.put("totalPages", studentsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }



    // ------------------ Get attendances by date ------------------
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AttendanceInfoDTO>> getByDate(@PathVariable String date) {
        List<Attendance> attendances = attendanceService.findByDate(LocalDate.parse(date));

        List<AttendanceInfoDTO> dtoList = attendances.stream()
                .map(a -> {
                    var student = a.getStudent();
                    String className = student.getClassroom() != null ? student.getClassroom().getClassName() : null;
                    String teacherName = student.getClassroom() != null && student.getClassroom().getTeacher() != null
                            ? student.getClassroom().getTeacher().getFirstName() + " " + student.getClassroom().getTeacher().getLastName()
                            : null;
                    String subjectName = a.getSubject() != null ? a.getSubject().getSubjectName() : null;

                    return new AttendanceInfoDTO(a.getId(), a.getDate(), a.isPresent(), subjectName, className, teacherName);
                }).toList();

        return ResponseEntity.ok(dtoList);
    }

    // ------------------ Helper method ------------------
    private StudentAttendanceDTO mapStudentAttendance(Student student, List<Attendance> attendances) {
        String className = student.getClassroom() != null ? student.getClassroom().getClassName() : null;
        String teacherName = student.getClassroom() != null && student.getClassroom().getTeacher() != null
                ? student.getClassroom().getTeacher().getFirstName() + " " + student.getClassroom().getTeacher().getLastName()
                : null;

        // Collect all subjects that have attendance
        List<String> subjectNames = attendances.stream()
                .filter(a -> a.getSubject() != null)
                .map(a -> a.getSubject().getSubjectName())
                .distinct()
                .toList();

        List<AttendanceInfoDTO> attendanceDTOs = attendances.stream()
                .map(a -> new AttendanceInfoDTO(
                        a.getId(),
                        a.getDate(),
                        a.isPresent(),
                        a.getSubject() != null ? a.getSubject().getSubjectName() : null,
                        className,
                        teacherName
                ))
                .toList();

        return new StudentAttendanceDTO(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getGender(),
                student.getDob() != null ? student.getDob().toString() : null,
                student.getPhoto(),
                className,
                subjectNames,  // now populated from attendance
                attendanceDTOs
        );
    }

}


//    @GetMapping("/date/{date}")
//    public ResponseEntity<List<StudentAttendanceDTO>> getAttendancesByDate(@PathVariable String date) {
//        LocalDate localDate = LocalDate.parse(date);
//        List<Attendance> attendances = attendanceService.findByDate(localDate);
//
//        if (attendances.isEmpty()) {
//            throw new MyResourceNotFoundException("No attendances found for date " + date);
//        }
//
//        // Group attendances by student
//        Map<Long, List<Attendance>> studentAttendanceMap = attendances.stream()
//                .collect(Collectors.groupingBy(a -> a.getStudent().getId()));
//
//        // Build DTOs
//        List<StudentAttendanceDTO> dtoList = studentAttendanceMap.entrySet().stream().map(entry -> {
//            List<Attendance> studentAttendances = entry.getValue();
//            var student = studentAttendances.get(0).getStudent();
//
//            // Subject names
//            List<String> subjectNames = student.getGrades().stream()
//                    .map(g -> g.getSubject().getSubjectName())
//                    .distinct()
//                    .toList();
//
//            // Class name
//            String className = student.getClassroom() != null ? student.getClassroom().getClassName() : null;
//
//            // Teacher name
//            String teacherName = null;
//            if (student.getClassroom() != null && student.getClassroom().getTeacher() != null) {
//                teacherName = student.getClassroom().getTeacher().getFirstName() + " " +
//                        student.getClassroom().getTeacher().getLastName();
//            }
//
//            // Map attendances to AttendanceInfoDTO
//            List<AttendanceInfoDTO> attendanceDTOs = studentAttendances.stream()
//                    .map(a -> {
//                        String subjectName = null;
//                        if (!student.getGrades().isEmpty()) {
//                            subjectName = student.getGrades().get(0).getSubject().getSubjectName();
//                        }
//                        return new AttendanceInfoDTO(
//                                a.getId(),
//                                a.getDate(),
//                                a.isPresent(),
//                                subjectName,
//                                className,
//                                teacherName
//                        );
//                    })
//                    .toList();
//
//            StudentAttendanceDTO dto = new StudentAttendanceDTO(
//                    student.getId(),
//                    student.getFirstName(),
//                    student.getLastName(),
//                    student.getGender(),
//                    student.getDob() != null ? student.getDob().toString() : null,
//                    student.getPhoto(),
//                    className,
//                    subjectNames,
//                    attendanceDTOs
//            );
//
//            // Add teacher name if StudentAttendanceDTO has field
//            try {
//                var method = dto.getClass().getMethod("setTeacherName", String.class);
//                method.invoke(dto, teacherName);
//            } catch (Exception ignored) {}
//
//            return dto;
//        }).toList();
//
//        return ResponseEntity.ok(dtoList);
//    }


// }