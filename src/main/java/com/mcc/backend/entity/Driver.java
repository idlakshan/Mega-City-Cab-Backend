package com.mcc.backend.entity;

public class Driver {
    private int driverId;
    private String name;
    private String address;
    private String email;
    private String licenseNumber;
    private String licenseImage;
    private String contactNumber;
    private String status;

    public Driver() {
    }

    public Driver(int driverId, String name, String address, String email, String licenseNumber, String licenseImage, String contactNumber, String status) {
        this.driverId = driverId;
        this.name = name;
        this.address = address;
        this.email = email;
        this.licenseNumber = licenseNumber;
        this.licenseImage = licenseImage;
        this.contactNumber = contactNumber;
        this.status = status;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseImage() {
        return licenseImage;
    }

    public void setLicenseImage(String licenseImage) {
        this.licenseImage = licenseImage;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
