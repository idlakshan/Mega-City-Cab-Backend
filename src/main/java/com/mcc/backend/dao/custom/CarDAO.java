package com.mcc.backend.dao.custom;

import com.mcc.backend.entity.Car;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CarDAO {
    boolean isCarNumberExists(Connection conn, String carNumber) throws SQLException;
    void saveCar(Connection conn, Car car) throws SQLException;
    List<Car> getAllVehicles(Connection conn) throws SQLException, ClassNotFoundException;
    boolean deleteCar(Connection conn, int carId) throws SQLException, ClassNotFoundException;
    Car getVehicleById(Connection conn, int carId) throws SQLException, ClassNotFoundException;
    boolean updateVehicle(Connection conn, Car car) throws SQLException, ClassNotFoundException;
}