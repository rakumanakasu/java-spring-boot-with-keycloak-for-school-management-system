package com.dara.su79.dto;

import java.util.List;

public class StudentAttendanceDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String photo;
    private String className;
    private List<String> subjects;
    private List<AttendanceInfoDTO> attendances;

    public StudentAttendanceDTO(Long id, String firstName, String lastName, String gender, String dob,
                                String photo, String className, List<String> subjects, List<AttendanceInfoDTO> attendances) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.photo = photo;
        this.className = className;
        this.subjects = subjects;
        this.attendances = attendances;
    }

    // Getters only
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getDob() { return dob; }
    public String getPhoto() { return photo; }
    public String getClassName() { return className; }
    public List<String> getSubjects() { return subjects; }
    public List<AttendanceInfoDTO> getAttendances() { return attendances; }
}
