package com.dara.su79.dto;

public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String address;
    private String email;
//    private String phone;
    private UserProfileDTO userProfile;
    private String photo;

    public StudentDTO(Long id, String firstName, String lastName, String gender, String dob,
                      String address, String email, UserProfileDTO userProfile, String photo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.email = email;
        this.userProfile = userProfile;
        this.photo = photo;
    }


    // Getters only for read-only DTO
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getDob() { return dob; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
//    public String getPhone() { return phone; }
    public String getPhoto() { return photo; }
    public UserProfileDTO getUserProfile() { return userProfile; }
}