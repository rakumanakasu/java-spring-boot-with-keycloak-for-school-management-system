package com.dara.su79.dto;

import java.util.List;

public class StudentDetailDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String address;
    private String email;
    private String photo;
    private ClassroomDTO classroom;
    private List<GradeDTO> grades;
    private List<AttendanceDTO> attendances;

    public StudentDetailDTO(Long id, String firstName, String lastName, String gender, String dob,
                            String address, String email, String photo,
                            ClassroomDTO classroom, List<GradeDTO> grades, List<AttendanceDTO> attendances) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.email = email;
        this.photo = photo;
        this.classroom = classroom;
        this.grades = grades;
        this.attendances = attendances;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getDob() { return dob; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoto() { return photo; }
    public ClassroomDTO getClassroom() { return classroom; }
    public List<GradeDTO> getGrades() { return grades; }
    public List<AttendanceDTO> getAttendances() { return attendances; }
}
