package com.dara.su79.dto;

public class SubjectUpdateDTO {
    private String subjectName;
    private String className;   // Flutter sends this

    public SubjectUpdateDTO() {}

    public SubjectUpdateDTO(String subjectName, String className) {
        this.subjectName = subjectName;
        this.className = className;
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
