package com.mcc.backend.entity;

public class UserDetails {
    private int id;
    private int user_Id;
    private int role_id;

    public UserDetails() {
    }

    public UserDetails(int id, int user_Id, int role_id) {
        this.id = id;
        this.user_Id = user_Id;
        this.role_id = role_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(int user_Id) {
        this.user_Id = user_Id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }
}
