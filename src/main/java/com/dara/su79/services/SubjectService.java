package com.dara.su79.services;

import com.dara.su79.dto.SubjectDTO;
import com.dara.su79.dto.SubjectResponseDTO;
import com.dara.su79.dto.SubjectUpdateDTO;
import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Classroom;
import com.dara.su79.models.Subject;
import com.dara.su79.repositories.ClassroomRepository;
import com.dara.su79.repositories.SubjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ClassroomRepository classroomRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository,
                          ClassroomRepository classroomRepository) {
        this.subjectRepository = subjectRepository;
        this.classroomRepository = classroomRepository;
    }

    // Find subject by ID
    public Subject findSubjectById(long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Subject with id " + id + " not found"));
    }

    // --- DTO methods ---
    public Page<SubjectResponseDTO> getAllSubjectsDTO(int page, int size) {
        Page<Subject> subjects = subjectRepository.findAll(PageRequest.of(page, size));
        List<SubjectResponseDTO> dtoList = subjects.stream()
                .map(s -> new SubjectResponseDTO(
                        s.getId(),
                        s.getSubjectName(),
                        s.getClassroom() != null ? s.getClassroom().getClassName() : null
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, subjects.getPageable(), subjects.getTotalElements());
    }

    @Transactional
    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public SubjectResponseDTO getSubjectDTOById(long id) {
        Subject s = findSubjectById(id);
        return new SubjectResponseDTO(
                s.getId(),
                s.getSubjectName(),
                s.getClassroom() != null ? s.getClassroom().getClassName() : null
        );
    }

    public Page<SubjectResponseDTO> searchByNameDTO(String name, int page, int size) {
        Page<Subject> subjects = subjectRepository.findBySubjectNameContainingIgnoreCase(name, PageRequest.of(page, size));
        List<SubjectResponseDTO> dtoList = subjects.stream()
                .map(s -> new SubjectResponseDTO(
                        s.getId(),
                        s.getSubjectName(),
                        s.getClassroom() != null ? s.getClassroom().getClassName() : null
                ))
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, PageRequest.of(page, size), subjects.getTotalElements());
    }

    @Transactional
    public void updateById(long id, SubjectUpdateDTO dto) {
        Subject existingSubject = findSubjectById(id);

        if (dto.getSubjectName() != null) {
            existingSubject.setSubjectName(dto.getSubjectName());
        }

        if (dto.getClassName() != null) {
            Classroom classroom = classroomRepository
                    .findByClassName(dto.getClassName())
                    .orElseThrow(() -> new MyResourceNotFoundException("Classroom '" + dto.getClassName() + "' not found"));
            existingSubject.setClassroom(classroom);
        }

        subjectRepository.save(existingSubject);
    }

    public void deleteSubject(long id) {
        if (!subjectRepository.existsById(id)) {
            throw new MyResourceNotFoundException("Subject with id " + id + " not found");
        }
        subjectRepository.deleteById(id);
    }

    // --- Subjects by className ---
    public List<SubjectDTO> getSubjectsByClass(String className) {
        List<Subject> subjects = subjectRepository.findByClassroomClassName(className);

        if (subjects.isEmpty()) {
            throw new MyResourceNotFoundException("No subjects found for class: " + className);
        }

        return subjects.stream()
                .map(sub -> new SubjectDTO(sub.getId(), sub.getSubjectName()))
                .collect(Collectors.toList());
    }

    // --- Subjects by classId ---
    public List<SubjectDTO> getSubjectsByClassId(Long classId) {
        List<Subject> subjects = subjectRepository.findByClassroomId(classId);

        if (subjects.isEmpty()) {
            throw new MyResourceNotFoundException("No subjects found for class ID: " + classId);
        }

        return subjects.stream()
                .map(sub -> new SubjectDTO(sub.getId(), sub.getSubjectName()))
                .collect(Collectors.toList());
    }
}
