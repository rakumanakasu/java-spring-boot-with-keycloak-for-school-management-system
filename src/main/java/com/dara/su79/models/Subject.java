package com.dara.su79.models;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subjectName;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    public Long getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public Classroom getClassroom() { return classroom; }

    public void setId(Long id) { this.id = id; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }
}
