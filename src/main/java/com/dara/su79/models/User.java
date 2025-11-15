package com.dara.su79.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private String email;
    private String role;

    private String firstName; //
    private String lastName;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private UserProfile userProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-student")
    private Student student;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-teacher")
    private Teacher teacher;


    @Column(name = "keycloak_id")
    private String keycloakId;

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getKeycloakId() { return keycloakId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Student getStudent() { return student; }
    public Teacher getTeacher() { return teacher; }
    public UserProfile getUserProfile() { return userProfile; }


    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setKeycloakId(String keycloakId) { this.keycloakId = keycloakId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setStudent(Student student) { this.student = student; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }
}
