package com.dara.su79.controllers;

import com.dara.su79.models.Student;
import com.dara.su79.models.Teacher;
import com.dara.su79.repositories.AttendanceRepository;
import com.dara.su79.repositories.GradeRepository;
import com.dara.su79.repositories.StudentRepository;
import com.dara.su79.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(Authentication authentication) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // ---------------- Extract JWT ----------------
            Jwt jwt;
            if (authentication.getPrincipal() instanceof Jwt principalJwt) {
                jwt = principalJwt;
            } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken token) {
                jwt = token.getToken();
            } else {
                throw new RuntimeException("Invalid authentication principal type");
            }

            String email = jwt.getClaimAsString("email");
            if (email == null || email.isEmpty()) {
                throw new RuntimeException("Email claim not found in JWT");
            }

            // ---------------- Map roles to Spring format ----------------
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .collect(Collectors.toList());

            System.out.println("Dashboard request for email: " + email + ", roles: " + roles);

            // ---------------- ADMIN ----------------
            if (roles.contains("ROLE_ADMIN")) {
                stats.put("students", safeCount(studentRepository.count()));
                stats.put("teachers", safeCount(teacherRepository.count()));
                stats.put("grades", safeCount(gradeRepository.count()));
                stats.put("attendance", safeCount(attendanceRepository.count()));

                // Total absent days for all students in last week
                List<Student> allStudents = studentRepository.findAll();
                long totalAbsent = allStudents.stream()
                        .mapToLong(s -> safeCount(attendanceRepository.countByStudentAndPresentFalseAndDateAfter(
                                s, LocalDate.now().minusWeeks(1))))
                        .sum();
                stats.put("absentCount", totalAbsent);
            }

            // ---------------- TEACHER ----------------
            else if (roles.contains("ROLE_TEACHER")) {
                Optional<Teacher> optionalTeacher = teacherRepository.findByEmail(email);
                if (optionalTeacher.isEmpty()) {
                    stats.put("students", 0);
                    stats.put("grades", 0);
                    stats.put("attendance", 0);
                    stats.put("absentCount", 0);
                    stats.put("warning", "Teacher record not found");
                } else {
                    Teacher teacher = optionalTeacher.get();
                    List<Student> students = Optional.ofNullable(teacher.getClassrooms())
                            .orElse(Collections.emptyList())
                            .stream()
                            .flatMap(c -> Optional.ofNullable(c.getStudents()).orElse(Collections.emptyList()).stream())
                            .collect(Collectors.toList());

                    long gradesCount = students.isEmpty() ? 0 : safeCount(gradeRepository.countByStudentIn(students));
                    long attendanceCount = students.isEmpty() ? 0 : safeCount(attendanceRepository.countByStudentIn(students));

                    stats.put("students", students.size());
                    stats.put("grades", gradesCount);
                    stats.put("attendance", attendanceCount);

                    // Total absent days for teacher's students
                    long totalAbsent = students.stream()
                            .mapToLong(s -> safeCount(attendanceRepository.countByStudentAndPresentFalseAndDateAfter(
                                    s, LocalDate.now().minusWeeks(1))))
                            .sum();
                    stats.put("absentCount", totalAbsent);

                    if (totalAbsent > 0) {
                        stats.put("warning", "Students absent for " + totalAbsent + " day(s) in the last week!");
                    }
                }
            }

            // ---------------- STUDENT ----------------
            else if (roles.contains("ROLE_STUDENT")) {
                Optional<Student> optionalStudent = studentRepository.findByEmail(email);
                if (optionalStudent.isEmpty()) {
                    stats.put("grades", 0);
                    stats.put("attendance", 0);
                    stats.put("absentCount", 0);
                    stats.put("warning", "Student record not found");
                } else {
                    Student student = optionalStudent.get();

                    long gradesCount = safeCount(gradeRepository.countByStudent(student));
                    long attendanceCount = safeCount(attendanceRepository.countByStudent(student));

                    stats.put("grades", gradesCount);
                    stats.put("attendance", attendanceCount);

                    long absentDays = safeCount(attendanceRepository.countByStudentAndPresentFalseAndDateAfter(
                            student, LocalDate.now().minusWeeks(1)
                    ));
                    stats.put("absentCount", absentDays);

                    if (absentDays > 0) {
                        stats.put("warning", "Absent for " + absentDays + " day(s) in the last week!");
                    }
                }
            }

            // ---------------- Other / Unknown Roles ----------------
            else {
                stats.put("message", "No stats available for your role");
                stats.put("absentCount", 0);
            }

            stats.put("role", roles);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage(),
                    "roles", authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList())
            ));
        }
    }


    // ---------------- Helper ----------------
    private long safeCount(Long value) {
        return value != null ? value : 0;
    }
}
