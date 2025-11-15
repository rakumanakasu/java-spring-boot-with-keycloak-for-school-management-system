package com.dara.su79.dto;

public class SubjectResponseDTO {
    private Long id;
    private String subjectName;
    private String className;

    public SubjectResponseDTO() {
    }

    public SubjectResponseDTO(Long id, String subjectName, String className) {
        this.id = id;
        this.subjectName = subjectName;
        this.className = className;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
