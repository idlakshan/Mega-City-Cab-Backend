package com.mcc.backend.entity;



import com.mcc.backend.dto.PaymentDTO;

import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private int userId;
    private int carId;
    private int driverId;
    private String pickupLocation;
    private String dropLocation;
    private Timestamp bookingDateTime;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String status;

    private Car car;
    private Driver driver;

    private Payment payment;

    public Booking() {
    }

    public Booking(int bookingId, int userId, int carId, int driverId, String pickupLocation, String dropLocation, Timestamp bookingDateTime, String customerName, String customerEmail, String customerPhone, String status, Car car, Driver driver, Payment payment) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.carId = carId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.bookingDateTime = bookingDateTime;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.status = status;
        this.car = car;
        this.driver = driver;
        this.payment = payment;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public Timestamp getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(Timestamp bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
