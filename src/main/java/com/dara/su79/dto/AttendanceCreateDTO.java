package com.dara.su79.dto;

import java.time.LocalDate;

public class AttendanceCreateDTO {

    private Long studentId;
    private Long subjectId;
    private boolean present;
    private LocalDate date;

    public AttendanceCreateDTO() {}

    public AttendanceCreateDTO(Long studentId, Long subjectId, boolean present, LocalDate date) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.present = present;
        this.date = date;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
