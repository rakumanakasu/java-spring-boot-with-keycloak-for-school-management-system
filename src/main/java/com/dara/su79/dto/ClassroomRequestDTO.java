package com.dara.su79.dto;

import java.util.List;

public class ClassroomRequestDTO {
    private String className;
    private String roomNumber;
    private Long teacherId; // Just send the teacher's ID
    private List<Long> studentIds; // Optional: list of student IDs to add

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public List<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
}