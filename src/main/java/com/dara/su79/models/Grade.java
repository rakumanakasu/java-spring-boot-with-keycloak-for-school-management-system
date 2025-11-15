package com.dara.su79.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double score;


    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonBackReference("student-grade")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    @JsonBackReference("subject-grade")
    private Subject subject;

    public Long getId() { return id; }
    public Double getScore() { return score; }
    public Student getStudent() { return student; }
    public Subject getSubject() { return subject; }

    public void setId(Long id) { this.id = id; }
    public void setScore(Double score) { this.score = score; }
    public void setStudent(Student student) { this.student = student; }
    public void setSubject(Subject subject) { this.subject = subject; }
}
