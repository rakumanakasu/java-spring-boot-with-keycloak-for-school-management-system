// SubjectDTO.java
package com.dara.su79.dto;

public class SubjectDTO {
    private Long id;
    private String name;

    public SubjectDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
