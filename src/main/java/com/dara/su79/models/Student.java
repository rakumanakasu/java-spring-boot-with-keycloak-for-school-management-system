package com.dara.su79.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

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

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    @JsonBackReference("classroom-student")
    private Classroom classroom;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("student-grade")
    private List<Grade> grades;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-student")
    private User user;

    public Long getId() { return id; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getGender() { return gender; }
    public LocalDate getDob() { return dob; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoto() { return photo; }
    public Classroom getClassroom() { return classroom; }
    public List<Grade> getGrades() { return grades; }
    public List<Attendance> getAttendances() { return attendances; }
    public User getUser() { return user; }

    public void setId(Long id) { this.id = id; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setGender(String gender) { this.gender = gender; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoto(String photo) { this.photo = photo; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }
    public void setUser(User user) { this.user = user; }
}
