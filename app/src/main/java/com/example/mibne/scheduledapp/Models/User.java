package com.example.mibne.scheduledapp.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String department;
    private String email;
    private String name;
    private String organization;
    private String phone;
    private String photoUrl;
    private String role;
    private String username;
    private String uid;

    public User() {
    }

    public User(String department, String email, String name,String organization,
                String phone, String photoUrl, String role, String username, String uid) {
        this.department = department;
        this.email = email;
        this.name = name;
        this.organization = organization;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.role = role;
        this.username = username;
        this.uid = uid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return  uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}