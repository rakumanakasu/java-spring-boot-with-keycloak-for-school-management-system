package com.dara.su79.dto;

public class GradeDTO {
    private Long id;
    private Double score;
    private String studentName;
    private String subjectName;

    public GradeDTO(Long id, Double score,String studentName, String subjectName) {
        this.id = id;
        this.score = score;
        this.studentName = studentName;
        this.subjectName = subjectName;
    }

    public Long getId() { return id; }
    public Double getScore() { return score; }
    public String getStudentName() { return studentName; }
    public String getSubjectName() { return subjectName; }
}
