package com.dara.su79.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendances")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private boolean present;


    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;


    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public boolean isPresent() { return present; }
    public Student getStudent() { return student; }
    public Subject getSubject() { return subject; }

    public void setId(Long id) { this.id = id; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setPresent(boolean present) { this.present = present; }
    public void setStudent(Student student) { this.student = student; }
    public void setSubject(Subject subject) { this.subject = subject; }
}
