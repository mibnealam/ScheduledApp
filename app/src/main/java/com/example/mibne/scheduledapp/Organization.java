package com.example.mibne.scheduledapp;

public class Organization {
    private String organizationName;
    private String shortName;
    private String departments;

    public Organization() {
    }

    public Organization(String organizationName, String shortName, String departments) {
        this.organizationName = organizationName;
        this.shortName = shortName;
        this.departments = departments;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }
}