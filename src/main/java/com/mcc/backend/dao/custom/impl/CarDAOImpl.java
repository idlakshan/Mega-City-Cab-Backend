package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.CarDAO;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.entity.Car;
import com.mcc.backend.servlet.VehicleServlet;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Override
    public List<CarDTO> getAllVehicles() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM car";
        List<CarDTO> vehicles = new ArrayList<>();
        try (Connection connection = VehicleServlet.dataSource.getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                CarDTO car = new CarDTO();
                car.setCarId(rs.getInt("car_id"));
                car.setCategoryId(rs.getInt("category_id"));
                car.setCarName(rs.getString("car_name"));
                car.setCarNumber(rs.getString("car_number"));
                car.setCarImage(rs.getString("car_image"));
                car.setStatus(rs.getString("status"));
                vehicles.add(car);
            }
        }
        return vehicles;
    }

    @Override
    public boolean deleteCar(int carId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM car WHERE car_id = ?";

        try (Connection conn = VehicleServlet.dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, carId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

}
