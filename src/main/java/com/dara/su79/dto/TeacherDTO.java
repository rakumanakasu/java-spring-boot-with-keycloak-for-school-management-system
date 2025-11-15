package com.dara.su79.dto;

import java.util.List;

public class TeacherDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String dob;
    private String gender;
    private Double salary;
    private String email;
    private String address;
    private String photo;
    private List<String> classrooms;

    // Constructor for StudentDetail view (8 args)
    public TeacherDTO(Long id, String firstName, String lastName, String email,
                      String address, String photo, List<String> classrooms,
                      Double salary, String dob, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.photo = photo;
        this.classrooms = classrooms != null ? classrooms : List.of();
        this.salary = salary;
        this.dob = dob;
        this.gender = gender;
    }




    // getters only
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhoto() { return photo; }
    public List<String> getClassrooms() { return classrooms; }
    public Double getSalary() { return salary; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
}
