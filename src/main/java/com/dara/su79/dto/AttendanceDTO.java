package com.dara.su79.dto;

import java.time.LocalDate;

public class AttendanceDTO {
    private Long id;
    private LocalDate date;
    private boolean present;

    public AttendanceDTO(Long id, LocalDate date, boolean present) {
        this.id = id;
        this.date = date;
        this.present = present;
    }

    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public boolean isPresent() { return present; }
}
