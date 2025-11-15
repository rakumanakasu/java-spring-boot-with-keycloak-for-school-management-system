package com.dara.su79.repositories;

import com.dara.su79.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    Page<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);
    List<Student> findByClassroomId(Long classroomId);
    Page<Student> findByClassroomId(Long classId, Pageable pageable);
    Page<Student> findByClassroomIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            Long classId, String firstNameKeyword, String lastNameKeyword, Pageable pageable);
}
