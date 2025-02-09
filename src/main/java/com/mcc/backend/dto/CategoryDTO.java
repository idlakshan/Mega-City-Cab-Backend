package com.mcc.backend.dto;

public class CategoryDTO {
    private int id;
    private String name;
    private String icon;
    private String title;
    private String features;
    private double price;

    public CategoryDTO() {
    }

    public CategoryDTO(String name, String icon, String title, String features, double price) {
        this.name = name;
        this.icon = icon;
        this.title = title;
        this.features = features;
        this.price = price;
    }

    public CategoryDTO(int id, String name, String icon, String title, String features, double price) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.title = title;
        this.features = features;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
