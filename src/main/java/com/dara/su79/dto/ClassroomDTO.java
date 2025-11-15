package com.dara.su79.dto;

import java.util.List;

public class ClassroomDTO {
    private Long id;
    private String className;
    private String roomNumber;
    private TeacherDTO teacher;
    private List<StudentDTO> students;

    public ClassroomDTO(Long id, String className, String roomNumber,
                        TeacherDTO teacher, List<StudentDTO> students) {
        this.id = id;
        this.className = className;
        this.roomNumber = roomNumber;
        this.teacher = teacher;
        this.students = students;
    }

    // Getters
    public Long getId() { return id; }
    public String getClassName() { return className; }
    public String getRoomNumber() { return roomNumber; }
    public TeacherDTO getTeacher() { return teacher; }
    public List<StudentDTO> getStudents() { return students; }
}