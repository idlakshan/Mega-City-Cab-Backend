package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.CarDAO;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.entity.Car;
import com.mcc.backend.servlet.VehicleServlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarDAOImpl implements CarDAO {

    @Override
    public boolean isCarNumberExists(Connection conn, String carNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM car WHERE car_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, carNumber);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    @Override
    public void saveCar(Connection conn, Car car) throws SQLException {
        String sql = "INSERT INTO car (category_Id, car_name, car_number, car_image) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, car.getCategoryId());
            pstmt.setString(2, car.getCarName());
            pstmt.setString(3, car.getCarNumber());
            pstmt.setString(4, car.getCarImage());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Car> getAllVehicles(Connection conn) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM car";
        List<Car> vehicles = new ArrayList<>();
        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                Car car = new Car();
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
    public boolean deleteCar(Connection conn, int carId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM car WHERE car_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, carId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public Car getVehicleById(Connection conn, int carId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM car WHERE car_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, carId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Car car = new Car();
                    car.setCarId(resultSet.getInt("car_id"));
                    car.setCategoryId(resultSet.getInt("category_id"));
                    car.setCarName(resultSet.getString("car_name"));
                    car.setCarNumber(resultSet.getString("car_number"));
                    car.setCarImage(resultSet.getString("car_image"));
                    car.setStatus(resultSet.getString("status"));
                    return car;
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateVehicle(Connection conn, Car car) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE car SET category_id = ?, car_name = ?, car_number = ?, car_image = ?, status = ? WHERE car_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, car.getCategoryId());
            ps.setString(2, car.getCarName());
            ps.setString(3, car.getCarNumber());
            ps.setString(4, car.getCarImage());
            ps.setString(5, car.getStatus());
            ps.setInt(6, car.getCarId());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    @Override
    public List<Car> getAvailableVehiclesByCategory(Connection conn, int categoryId) throws Exception {
        String sql = "SELECT * FROM car WHERE category_id = ? AND status = 'Available'";
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Car car = new Car();
                car.setCarId(rs.getInt("car_id"));
                car.setCategoryId(rs.getInt("category_id"));
                car.setCarName(rs.getString("car_name"));
                car.setCarNumber(rs.getString("car_number"));
                car.setCarImage(rs.getString("car_image"));
                car.setStatus(rs.getString("status"));
                cars.add(car);
            }
        }
        return cars;
    }
}