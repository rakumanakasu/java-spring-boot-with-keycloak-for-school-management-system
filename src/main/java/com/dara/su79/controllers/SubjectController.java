package com.dara.su79.controllers;

import com.dara.su79.dto.SubjectCreateDTO;
import com.dara.su79.dto.SubjectDTO;
import com.dara.su79.dto.SubjectResponseDTO;
import com.dara.su79.dto.SubjectUpdateDTO;
import com.dara.su79.exceptions.MyResourceNotFoundException;
import com.dara.su79.models.Classroom;
import com.dara.su79.models.Subject;
import com.dara.su79.repositories.ClassroomRepository;
import com.dara.su79.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subject")
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("")
    public ResponseEntity<Page<SubjectResponseDTO>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SubjectResponseDTO> subjects = subjectService.getAllSubjectsDTO(page, size);
        return new ResponseEntity<>(subjects, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> createSubject(@RequestBody SubjectCreateDTO dto) {
        // Lookup classroom by className
        Classroom classroom = classroomRepository.findByClassName(dto.getClassName())
                .orElseThrow(() -> new MyResourceNotFoundException("Classroom not found"));

        Subject subject = new Subject();
        subject.setSubjectName(dto.getSubjectName());
        subject.setClassroom(classroom); // link classroom

        subjectService.createSubject(subject);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Subject created successfully!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDTO> findById(@PathVariable long id) {
        SubjectResponseDTO subjectDTO = subjectService.getSubjectDTOById(id);
        return new ResponseEntity<>(subjectDTO, HttpStatus.OK);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByClassId(@PathVariable Long classId) {
        List<SubjectDTO> subjects = subjectService.getSubjectsByClassId(classId);
        return ResponseEntity.ok(subjects);
    }



    @GetMapping("/searchByName")
    public ResponseEntity<Page<SubjectResponseDTO>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SubjectResponseDTO> subjects = subjectService.searchByNameDTO(name, page, size);
        return new ResponseEntity<>(subjects, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateSubject(
            @PathVariable long id,
            @RequestBody SubjectUpdateDTO dto) {

        subjectService.updateById(id, dto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Subject updated successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSubject(@PathVariable long id) {
        subjectService.deleteSubject(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Subject deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
