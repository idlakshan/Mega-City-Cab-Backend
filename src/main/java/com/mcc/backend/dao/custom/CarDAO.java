package com.mcc.backend.dao.custom;

import com.mcc.backend.entity.Car;

import java.sql.SQLException;

public interface CarDAO {
    boolean isCarNumberExists(String carNumber) throws SQLException;
    void saveCar(Car car) throws SQLException;
}
