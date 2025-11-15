package com.dara.su79.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "UserProfile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Long phone;
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    public int getId() { return id; }
    public Long getPhone() { return phone; }
    public String getAddress() { return address; }
    public User getUser() { return user; }


    public void setPhone(Long phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setUser(User user) { this.user = user; }
}
