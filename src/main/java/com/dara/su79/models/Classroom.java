package com.dara.su79.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;
    private String roomNumber;



    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonBackReference("teacher-classroom")
    private Teacher teacher;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("classroom-student")
    private List<Student> students;

    public Classroom() {}


    public Long getId() { return id; }
    public String getClassName() { return className; }
    public String getRoomNumber() { return roomNumber; }
    public Teacher getTeacher() { return teacher; }
    public List<Student> getStudents() { return students; }

    public void setId(Long id) { this.id = id; }
    public void setClassName(String className) { this.className = className; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public void setStudents(List<Student> students) { this.students = students; }
}
