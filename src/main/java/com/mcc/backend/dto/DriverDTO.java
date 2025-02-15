package com.mcc.backend.dto;

public class DriverDTO {
    private Integer driverId;
    private String driverName;
    private String driverNic;
    private String driverAddress;
    private String driverEmail;
    private String licenseImage;
    private String driverContact;
    private String status;

    public DriverDTO() {
    }

    public DriverDTO(Integer driverId, String driverName, String driverNic, String driverAddress, String driverEmail, String licenseImage, String driverContact, String status) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverNic = driverNic;
        this.driverAddress = driverAddress;
        this.driverEmail = driverEmail;
        this.licenseImage = licenseImage;
        this.driverContact = driverContact;
        this.status = status;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverNic() {
        return driverNic;
    }

    public void setDriverNic(String driverNic) {
        this.driverNic = driverNic;
    }

    public String getDriverAddress() {
        return driverAddress;
    }

    public void setDriverAddress(String driverAddress) {
        this.driverAddress = driverAddress;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getLicenseImage() {
        return licenseImage;
    }

    public void setLicenseImage(String licenseImage) {
        this.licenseImage = licenseImage;
    }

    public String getDriverContact() {
        return driverContact;
    }

    public void setDriverContact(String driverContact) {
        this.driverContact = driverContact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
