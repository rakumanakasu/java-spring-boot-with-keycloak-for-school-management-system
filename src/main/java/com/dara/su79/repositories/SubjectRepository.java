package com.dara.su79.repositories;

import com.dara.su79.models.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Search by subject name
    List<Subject> findBySubjectNameContainingIgnoreCase(String name);
    Page<Subject> findBySubjectNameContainingIgnoreCase(String name, Pageable pageable);

    // Search by className
    List<Subject> findByClassroomClassName(String className);

    // Search by classroom ID
    List<Subject> findByClassroomId(Long classId);
}
