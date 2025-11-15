package com.dara.su79.repositories;

import com.dara.su79.models.Attendance;
import com.dara.su79.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Find all attendance records for a student
    List<Attendance> findByStudentId(Long studentId);

    // Find all attendance records for a student within a date range
    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);

    // Find all attendance records on a specific date
    List<Attendance> findByDate(LocalDate date);
    long countByStudentIn(List<Student> students);
    long countByStudent(Student student);
    Optional<Attendance> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);
    long countByStudentAndPresentFalseAndDateAfter(Student student, LocalDate date);


}

