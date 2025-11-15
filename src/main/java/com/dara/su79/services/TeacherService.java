package com.dara.su79.services;

import com.dara.su79.configurations.FileUploadUtil;
import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Teacher;
import com.dara.su79.models.User;
import com.dara.su79.repositories.TeacherRepository;
import com.dara.su79.repositories.UserRepository;
import com.dara.su79.dto.TeacherDTO;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.address:localhost}")
    private String serverAddress;

    @Value("${server.port:8081}")
    private String serverPort;

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository,
                          UserRepository userRepository,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // -------------------- GET ALL --------------------
    public Page<TeacherDTO> getAllTeachers(int page, int size) {
        Page<Teacher> teachers = teacherRepository.findAll(PageRequest.of(page, size));
        return teachers.map(this::convertToDTO);
    }

    // -------------------- CREATE TEACHER --------------------
    @Transactional
    public Teacher createTeacher(Teacher teacher, MultipartFile file) throws IOException {
        // Handle photo upload
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            FileUploadUtil.saveFile(uploadDir, fileName, file);
            teacher.setPhoto(fileName);
        } else {
            teacher.setPhoto("");
        }
        Teacher savedTeacher = teacherRepository.save(teacher);

        //Create user account for teacher
        if (savedTeacher.getUser() == null) {
            User user = new User();
            user.setUsername(generateUsername(savedTeacher.getLastName()));
            user.setEmail(savedTeacher.getEmail());
            user.setFirstName(savedTeacher.getFirstName());
            user.setLastName(savedTeacher.getLastName());
            user.setRole("TEACHER");
            user.setTeacher(savedTeacher);
            savedTeacher.setUser(user);
            String rawPassword = "123@";
            userService.createUser(user, rawPassword);
            teacherRepository.save(savedTeacher);
        }

        return savedTeacher;
    }


    private String generateUsername(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return "teacher" + System.currentTimeMillis();
        }
        return lastName.toLowerCase();
    }

    // -------------------- FIND BY ID --------------------
    public TeacherDTO findTeacherById(long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Teacher with id " + id + " not found"));
        return convertToDTO(teacher);
    }

    // -------------------- SEARCH BY NAME --------------------
    public Page<TeacherDTO> findByName(String name, int page, int size) {
        Page<Teacher> teachers = teacherRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        name, name, PageRequest.of(page, size));
        return teachers.map(this::convertToDTO);
    }

    // -------------------- UPDATE --------------------
    @Transactional
    public Teacher updateById(long id, Teacher teacher, MultipartFile file) throws IOException {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Teacher with id " + id + " not found"));

        existingTeacher.setFirstName(teacher.getFirstName());
        existingTeacher.setLastName(teacher.getLastName());
        existingTeacher.setGender(teacher.getGender());
        existingTeacher.setDob(teacher.getDob());
        existingTeacher.setAddress(teacher.getAddress());
        existingTeacher.setEmail(teacher.getEmail());
        existingTeacher.setSalary(teacher.getSalary());

        if (file != null && !file.isEmpty()) {
            if (existingTeacher.getPhoto() != null && !existingTeacher.getPhoto().isEmpty()) {
                FileUploadUtil.removePhoto(uploadDir, existingTeacher.getPhoto());
            }
            String newFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            FileUploadUtil.saveFile(uploadDir, newFileName, file);
            existingTeacher.setPhoto(newFileName);
        }

        // Update linked user
        User user = existingTeacher.getUser();
        if (user != null) {
            user.setFirstName(teacher.getFirstName());
            user.setLastName(teacher.getLastName());
            user.setEmail(teacher.getEmail());

            // ✅ Update username also to match new last name
            user.setUsername(generateUsername(teacher.getLastName()));

            userRepository.save(user);
        }

        return teacherRepository.save(existingTeacher);
    }

    // -------------------- DELETE --------------------
    @Transactional
    public void deleteTeacher(long id) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Teacher with id " + id + " not found"));

        // Delete teacher photo
        try {
            if (existingTeacher.getPhoto() != null && !existingTeacher.getPhoto().isEmpty()) {
                FileUploadUtil.removePhoto(uploadDir, existingTeacher.getPhoto());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete photo: " + e.getMessage());
        }

        // Delete linked user
        if (existingTeacher.getUser() != null) {
            userService.deleteUser(existingTeacher.getUser().getId());
        }

        teacherRepository.delete(existingTeacher);
    }

    // -------------------- CONVERT TO DTO --------------------
    private TeacherDTO convertToDTO(Teacher t) {
        List<String> classroomNames = t.getClassrooms() != null ?
                t.getClassrooms().stream()
                        .map(c -> c.getClassName())
                        .collect(Collectors.toList()) : null;

        return new TeacherDTO(
                t.getId(),
                t.getFirstName(),
                t.getLastName(),
                t.getEmail(),
                t.getAddress(),
                t.getPhoto() != null ? "http://" + serverAddress + ":" + serverPort + "/uploads/" + t.getPhoto() : "",
                classroomNames,
                t.getSalary(),
                t.getDob() != null ? t.getDob().toString() : null,
                t.getGender()
        );
    }
}
