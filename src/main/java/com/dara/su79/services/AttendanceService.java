package com.dara.su79.services;

import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Attendance;
import com.dara.su79.repositories.AttendanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public Page<Attendance> getAllAttendances(int page, int size) {
        return attendanceRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public Attendance findById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Attendance not found with id " + id));
    }

    @Transactional
    public Attendance createAttendanceIfNotExists(Attendance attendance) {
        Long studentId = attendance.getStudent().getId();
        Long subjectId = attendance.getSubject() != null ? attendance.getSubject().getId() : null;
        LocalDate date = attendance.getDate();

        if (subjectId != null) {
            Optional<Attendance> existing = attendanceRepository
                    .findByStudentIdAndSubjectIdAndDate(studentId, subjectId, date);

            if (existing.isPresent()) {
                throw new IllegalStateException("Attendance already exists for this student, subject, and date");
            }
        }

        return attendanceRepository.save(attendance);
    }


//    @Transactional
//    public Attendance updateAttendance(Long id, Attendance attendance) {
//        Attendance existing = findById(id);
//        existing.setDate(attendance.getDate());
//        existing.setPresent(attendance.isPresent());
//        existing.setStudent(attendance.getStudent());
//        return attendanceRepository.save(existing);
//    }

    @Transactional
    public Attendance updateAttendancePresent(Long id, Boolean present) {
        Attendance existing = findById(id);

        existing.setPresent(present);
//        existing.setDate(LocalDate.now());

        return attendanceRepository.save(existing);
    }




    @Transactional
    public void deleteAttendance(Long id) {
        Attendance existing = findById(id);
        attendanceRepository.delete(existing);
    }

    public List<Attendance> findByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<Attendance> findByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
}
