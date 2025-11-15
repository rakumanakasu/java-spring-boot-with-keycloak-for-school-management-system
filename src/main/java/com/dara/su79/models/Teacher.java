package com.dara.su79.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;
    private String firstName;
    private String gender;
    private LocalDate dob;
    private String address;
    private String email;
    private String photo;
    private Double salary;


    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("teacher-classroom")
    private List<Classroom> classrooms;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-teacher")
    private User user;


    public Teacher() {}


    public Long getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getGender() { return gender; }
    public LocalDate getDob() { return dob; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoto() { return photo; }
    public Double getSalary() { return salary; }
    public User getUser() { return user; }
    public List<Classroom> getClassrooms() { return classrooms; }


    public void setId(Long id) { this.id = id; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setGender(String gender) { this.gender = gender; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoto(String photo) { this.photo = photo; }
    public void setSalary(Double salary) { this.salary = salary; }
    public void setClassrooms(List<Classroom> classrooms) { this.classrooms = classrooms; }
    public void setUser(User user) { this.user = user; }
}
