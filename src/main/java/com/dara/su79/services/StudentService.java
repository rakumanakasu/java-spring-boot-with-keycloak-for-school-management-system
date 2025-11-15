package com.dara.su79.services;

import com.dara.su79.configurations.FileUploadUtil;
import com.dara.su79.dto.*;
import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Classroom;
import com.dara.su79.models.Student;
import com.dara.su79.models.User;
import com.dara.su79.repositories.ClassroomRepository;
import com.dara.su79.repositories.StudentRepository;
import com.dara.su79.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ClassroomRepository classroomRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.address:localhost}")
    private String serverAddress;

    @Value("${server.port:8081}")
    private String serverPort;

    public StudentService(StudentRepository studentRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          UserService userService,
                          ClassroomRepository classroomRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.classroomRepository = classroomRepository;
    }

    // ------------------ Helpers ------------------
    private String getPhotoUrl(String filename) {
        if (filename != null && !filename.isEmpty()) {
            return "http://" + serverAddress + ":" + serverPort + "/uploads/" + filename;
        }
        return null;
    }

    private void prependPhotoUrl(Page<Student> students) {
        students.getContent().forEach(this::prependPhotoUrlForEntity);
    }

    private void prependPhotoUrlForEntity(Student s) {
        // Do NOT modify entity's photo; this is only for display in DTOs
        // So this method is optional if you always use getPhotoUrl() in DTO creation
    }

    public List<Student> findByClassId(Long classId) {
        return studentRepository.findByClassroomId(classId);
    }
    public Page<Student> getStudentsByClassId(Long classId, PageRequest pageRequest) {
        return studentRepository.findByClassroomId(classId, pageRequest);
    }




    public Page<Student> getAllStudents(int page, int size) {
        return studentRepository.findAll(PageRequest.of(page, size));
    }

    // ------------------ CRUD ------------------
    public Page<StudentDTO> getAllStudentsDTO(int page, int size) {
        Page<Student> students = studentRepository.findAll(PageRequest.of(page, size));
        return students.map(s -> new StudentDTO(
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
                getPhotoUrl(s.getPhoto())
        ));
    }

    @Transactional
    public Student createStudent(Student student, MultipartFile file, Long classroomId) {
        try {
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                FileUploadUtil.saveFile(uploadDir, fileName, file);
                student.setPhoto(fileName);
            }

            if (student.getGender() == null) student.setGender("Male");

            // Set classroom if provided
            if (classroomId != null) {
                Classroom classroom = classroomRepository.findById(classroomId)
                        .orElseThrow(() -> new MyResourceNotFoundException("Classroom not found with id " + classroomId));
                student.setClassroom(classroom);
            }

            // Create user if not exists
            if (student.getUser() == null) {
                User user = new User();
                user.setUsername(generateUsername(student.getFirstName(), student.getLastName()));
                String rawPassword = "123456";
                user.setEmail(student.getEmail());
                user.setFirstName(student.getFirstName());
                user.setLastName(student.getLastName());
                user.setRole("STUDENT");

                user.setStudent(student);
                student.setUser(user);

                userService.createUser(user, rawPassword);
            }

            return studentRepository.save(student);

        } catch (IOException e) {
            throw new RuntimeException("Error saving student photo", e);
        }
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = lastName.toLowerCase();
        String username = baseUsername;
        int count = 1;

        // Ensure unique username (optional but recommended)
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + count;
            count++;
        }

        return username;
    }

    public Page<Student> searchStudentsByClassAndName(Long classId, String keyword, Pageable pageable) {
        return studentRepository.findByClassroomIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                classId, keyword, keyword, pageable
        );
    }


    public Student findStudentById(long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Student with id " + id + " not found"));
    }

    public Page<Student> findByName(String name, int page, int size) {
        Page<Student> students = studentRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, PageRequest.of(page, size));
        return students;
    }

    @Transactional
    public Student updateStudent(long id, Student updatedData, MultipartFile file, Long classroomId) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Student with id " + id + " not found"));

        try {
            if (file != null && !file.isEmpty()) {
                if (existing.getPhoto() != null && !existing.getPhoto().isEmpty()) {
                    FileUploadUtil.removePhoto(uploadDir, existing.getPhoto());
                }
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                FileUploadUtil.saveFile(uploadDir, fileName, file);
                existing.setPhoto(fileName);
            }

            existing.setFirstName(updatedData.getFirstName());
            existing.setLastName(updatedData.getLastName());
            existing.setGender(updatedData.getGender() != null ? updatedData.getGender() : "Male");
            existing.setDob(updatedData.getDob());
            existing.setAddress(updatedData.getAddress());
            existing.setEmail(updatedData.getEmail());

            // Update classroom if provided
            if (classroomId != null) {
                Classroom classroom = classroomRepository.findById(classroomId)
                        .orElseThrow(() -> new MyResourceNotFoundException("Classroom not found with id " + classroomId));
                existing.setClassroom(classroom);
            }

            User user = existing.getUser();
            if (user != null) {
                user.setFirstName(updatedData.getFirstName());
                user.setLastName(updatedData.getLastName());
                user.setEmail(updatedData.getEmail());
                userRepository.save(user);
            }

            return studentRepository.save(existing);

        } catch (IOException e) {
            throw new RuntimeException("Error updating student photo", e);
        }
    }

    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new MyResourceNotFoundException("Student not found with email: " + email));
    }

    @Transactional
    public void deleteStudent(long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Student with id " + id + " not found"));

        // -------------------- 1. Remove student photo --------------------
        if (student.getPhoto() != null && !student.getPhoto().isEmpty()) {
            try {
                FileUploadUtil.removePhoto(uploadDir, student.getPhoto());
            } catch (IOException e) {
                // Log but don’t fail deletion because of photo
                System.err.println("Failed to remove photo: " + e.getMessage());
            }
        }

        // -------------------- 2. Delete linked User --------------------
        if (student.getUser() != null) {
            try {
                // Delete user in Keycloak and local DB
                userService.deleteUser(student.getUser().getId());
            } catch (Exception e) {
                // Log error but continue
                System.err.println("Failed to delete linked user: " + e.getMessage());
            }
        }


        // -------------------- 3. Delete the Student --------------------
        studentRepository.delete(student);
    }


    // ------------------ Student Detail DTO ------------------
    public StudentDetailDTO getStudentDetail(long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Student not found"));

        ClassroomDTO classroomDTO = null;
        if (s.getClassroom() != null) {
            TeacherDTO teacherDTO = null;
            if (s.getClassroom().getTeacher() != null) {
                teacherDTO = new TeacherDTO(
                        s.getClassroom().getTeacher().getId(),
                        s.getClassroom().getTeacher().getFirstName(),
                        s.getClassroom().getTeacher().getLastName(),
                        s.getClassroom().getTeacher().getEmail(),
                        s.getClassroom().getTeacher().getAddress(),
                        getPhotoUrl(s.getClassroom().getTeacher().getPhoto()),
                        List.of(),
                        s.getClassroom().getTeacher().getSalary(),
                        s.getClassroom().getTeacher().getDob() != null
                                ? s.getClassroom().getTeacher().getDob().toString()
                                : null,
                        s.getClassroom().getTeacher().getGender()
                );
            }

            classroomDTO = new ClassroomDTO(
                    s.getClassroom().getId(),
                    s.getClassroom().getClassName(),
                    s.getClassroom().getRoomNumber(),
                    teacherDTO,
                    s.getClassroom().getStudents() != null
                            ? s.getClassroom().getStudents().stream()
                            .map(st -> new StudentDTO(
                                    st.getId(),
                                    st.getFirstName(),
                                    st.getLastName(),
                                    st.getGender(),
                                    st.getDob() != null ? st.getDob().toString() : null,
                                    st.getAddress(),
                                    st.getEmail(),
                                    st.getUser() != null && st.getUser().getUserProfile() != null
                                            ? new UserProfileDTO(
                                            st.getUser().getUserProfile().getPhone(),
                                            st.getUser().getUserProfile().getAddress()
                                    )
                                            : null,
                                    getPhotoUrl(st.getPhoto())
                            ))
                            .collect(Collectors.toList())
                            : List.of()
            );
        }

        List<GradeDTO> gradeDTOs = s.getGrades() != null
                ? s.getGrades().stream()
                .map(g -> new GradeDTO(
                        g.getId(),
                        g.getScore(),
                        g.getStudent() != null
                                ? g.getStudent().getFirstName() + " " + g.getStudent().getLastName()
                                : null,
                        g.getSubject() != null
                                ? g.getSubject().getSubjectName()
                                : null
                ))
                .collect(Collectors.toList())
                : List.of();

        List<AttendanceDTO> attendanceDTOs = s.getAttendances() != null
                ? s.getAttendances().stream()
                .map(a -> new AttendanceDTO(a.getId(), a.getDate(), a.isPresent()))
                .collect(Collectors.toList())
                : List.of();

        return new StudentDetailDTO(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getGender(),
                s.getDob() != null ? s.getDob().toString() : null,
                s.getAddress(),
                s.getEmail(),
                getPhotoUrl(s.getPhoto()),
                classroomDTO,
                gradeDTOs,
                attendanceDTOs
        );
    }
}
