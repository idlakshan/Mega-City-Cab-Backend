package com.mcc.backend.dto;

public class CarDTO {
    private int carId;
    private int categoryId;
    private String carName;
    private String carNumber;
    private String carImage;
    private String status;


    public CarDTO() {
    }

    public CarDTO(int carId, int categoryId, String carName, String carNumber, String carImage, String status) {
        this.carId = carId;
        this.categoryId = categoryId;
        this.carName = carName;
        this.carNumber = carNumber;
        this.carImage = carImage;
        this.status = status;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
