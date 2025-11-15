package com.dara.su79.repositories;

import com.dara.su79.models.Grade;
import com.dara.su79.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    long countByStudentIn(List<Student> students);
    long countByStudent(Student student);

    // Find all grades for a specific student
    List<Grade> findByStudentId(Long studentId);

    // Find all grades for a specific subject
    List<Grade> findBySubjectId(Long subjectId);

    // Find all grades for a specific student and subject
    List<Grade> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
}
