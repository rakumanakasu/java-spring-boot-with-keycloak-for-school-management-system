package com.dara.su79.services;

import com.dara.su79.dto.GradeDTO;
import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Grade;
import com.dara.su79.repositories.GradeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    // ------------------- Get paged DTOs -------------------
    public Page<GradeDTO> getAllGradesDTO(int page, int size) {
        Page<Grade> grades = gradeRepository.findAll(PageRequest.of(page, size));

        return grades.map(this::convertToDTO);
    }

    // ------------------- CRUD -------------------
    @Transactional
    public Grade createGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    public Grade findById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Grade not found with id " + id));
    }

    @Transactional
    public Grade updateGrade(Long id, Grade grade) {
        Grade existing = findById(id);
        existing.setScore(grade.getScore());
        existing.setStudent(grade.getStudent());
        existing.setSubject(grade.getSubject());
        return gradeRepository.save(existing);
    }

    public void deleteGrade(Long id) {
        Grade existing = findById(id);
        gradeRepository.delete(existing);
    }

    // ------------------- DTO conversion -------------------
    public GradeDTO convertToDTO(Grade grade) {
        String studentName = grade.getStudent() != null
                ? grade.getStudent().getFirstName() + " " + grade.getStudent().getLastName()
                : null;

        String subjectName = grade.getSubject() != null ? grade.getSubject().getSubjectName() : null;

        return new GradeDTO(grade.getId(), grade.getScore(), studentName, subjectName);
    }

    // ------------------- Search helpers -------------------
    public List<GradeDTO> findByStudent(Long studentId) {
        return gradeRepository.findByStudentId(studentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GradeDTO> findBySubject(Long subjectId) {
        return gradeRepository.findBySubjectId(subjectId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GradeDTO> findByStudentAndSubject(Long studentId, Long subjectId) {
        return gradeRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
