package com.dara.su79.services;


import com.dara.su79.dto.*;
import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Classroom;
import com.dara.su79.models.Student;
import com.dara.su79.models.Teacher;
import com.dara.su79.repositories.ClassroomRepository;
import com.dara.su79.repositories.StudentRepository;
import com.dara.su79.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;


    public ClassroomService(ClassroomRepository classroomRepository,
                            TeacherRepository teacherRepository,
                            StudentRepository studentRepository) {
        this.classroomRepository = classroomRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    public Page<Classroom> getAllClassrooms(int page, int size) {
        return classroomRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Classroom createClassroom(ClassroomRequestDTO dto) {
        Classroom classroom = new Classroom();
        classroom.setClassName(dto.getClassName());
        classroom.setRoomNumber(dto.getRoomNumber());

        if (dto.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new MyResourceNotFoundException("Teacher not found with id " + dto.getTeacherId()));
            classroom.setTeacher(teacher);
        }

        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            List<Student> students = studentRepository.findAllById(dto.getStudentIds());
            classroom.setStudents(students);
        }

        return classroomRepository.save(classroom);
    }

    public Classroom findById(Long id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Classroom not found with id " + id));
    }


    @Transactional
    public Classroom updateClassroom(Long id, ClassroomRequestDTO dto) {
        Classroom existing = classroomRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Classroom not found with id " + id));

        existing.setClassName(dto.getClassName());
        existing.setRoomNumber(dto.getRoomNumber());

        if (dto.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new MyResourceNotFoundException("Teacher not found with id " + dto.getTeacherId()));
            existing.setTeacher(teacher);
        }

        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            List<Student> students = studentRepository.findAllById(dto.getStudentIds());
            existing.setStudents(students);
        }

        return classroomRepository.save(existing);
    }

    @Transactional
    public void deleteClassroom(Long id) {
        Classroom existing = classroomRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Classroom not found with id " + id));
        classroomRepository.delete(existing);
    }
    // -------------------- CONVERT CLASSROOM TO DTO --------------------
    public ClassroomDTO convertToDTO(Classroom classroom) {
        // Teacher DTO
        TeacherDTO teacherDTO = null;
        if (classroom.getTeacher() != null) {
            Teacher t = classroom.getTeacher();

            // Teacher classrooms names
            List<String> teacherClassrooms = t.getClassrooms() != null
                    ? t.getClassrooms().stream()
                    .map(c -> c.getClassName())
                    .collect(Collectors.toList())
                    : List.of();

            teacherDTO = new TeacherDTO(
                    t.getId(),
                    t.getFirstName(),
                    t.getLastName(),
                    t.getEmail(),
                    t.getAddress(),
                    t.getPhoto() != null
                            ? "http://" + serverAddress + ":" + serverPort + "/uploads/" + t.getPhoto()
                            : "",
                    teacherClassrooms,
                    t.getSalary(),
                    t.getDob() != null ? t.getDob().toString() : null,
                    t.getGender()
            );
        }

        // Student DTOs
        List<StudentDTO> studentDTOs = classroom.getStudents() != null
                ? classroom.getStudents().stream()
                .map(s -> new StudentDTO(
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
                        s.getPhoto() != null
                                ? "http://" + serverAddress + ":" + serverPort + "/uploads/" + s.getPhoto()
                                : ""
                ))
                .collect(Collectors.toList())
                : List.of();


        // Classroom DTO
        return new ClassroomDTO(
                classroom.getId(),
                classroom.getClassName(),
                classroom.getRoomNumber(),
                teacherDTO,
                studentDTOs
        );
    }




}
