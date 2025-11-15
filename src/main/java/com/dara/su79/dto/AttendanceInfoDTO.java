package com.dara.su79.dto;

import java.time.LocalDate;

public class AttendanceInfoDTO {
    private Long id;
    private LocalDate date;
    private boolean present;
    private String subjectName;
    private String className;
    private String teacherName; // <-- new field

    public AttendanceInfoDTO(Long id, LocalDate date, boolean present, String subjectName, String className, String teacherName) {
        this.id = id;
        this.date = date;
        this.present = present;
        this.subjectName = subjectName;
        this.className = className;
        this.teacherName = teacherName;
    }

    // Getters
    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public boolean isPresent() { return present; }
    public String getSubjectName() { return subjectName; }
    public String getClassName() { return className; }
    public String getTeacherName() { return teacherName; } // new getter
}
