package com.dara.su79.dto;

public class UserProfileDTO {
    private Long phone;
    private String address;

    public UserProfileDTO(Long phone, String address) {
        this.phone = phone;
        this.address = address;
    }

    public Long getPhone() { return phone; }
    public String getAddress() { return address; }
}
