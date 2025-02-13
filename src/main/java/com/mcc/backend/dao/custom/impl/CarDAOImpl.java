package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.CarDAO;
import com.mcc.backend.entity.Car;
import com.mcc.backend.servlet.VehicleServlet;

import java.sql.*;

public class CarDAOImpl implements CarDAO {


    @Override
    public boolean isCarNumberExists(String carNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM car WHERE car_number = ?";
        try (Connection conn = VehicleServlet.dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carNumber);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new SQLException("Error checking car number in database", e);
        }
    }

    @Override
    public void saveCar(Car car) throws SQLException {
        String sql = "INSERT INTO car (category_Id, car_name, car_number, car_image) VALUES (?, ?, ?, ?)";
        try (Connection conn = VehicleServlet.dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, car.getCategoryId());
            pstmt.setString(2, car.getCarName());
            pstmt.setString(3, car.getCarNumber());
            pstmt.setString(4, car.getCarImage());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error saving car to database", e);
        }
    }
}
