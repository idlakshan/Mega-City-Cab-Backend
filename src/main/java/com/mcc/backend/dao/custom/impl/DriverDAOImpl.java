package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.DriverDAO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.entity.Driver;
import com.mcc.backend.servlet.VehicleServlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriverDAOImpl implements DriverDAO {

    @Override
    public boolean saveDriver(Connection conn, Driver driver) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO driver (driver_name, driver_nic, driver_address, driver_email, license_image, driver_contact) VALUES ( ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, driver.getDriverName());
            ps.setString(2, driver.getDriverNic());
            ps.setString(3, driver.getDriverAddress());
            ps.setString(4, driver.getDriverEmail());
            ps.setString(5, driver.getLicenseImage());
            ps.setString(6, driver.getDriverContact());

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
        }
    }

    @Override
    public List<Driver> getAllDrivers(Connection conn) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM driver";
        List<Driver> drivers = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Driver driver = new Driver(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getString("driver_nic"),
                        rs.getString("driver_address"),
                        rs.getString("driver_email"),
                        rs.getString("license_image"),
                        rs.getString("driver_contact"),
                        rs.getString("status")
                );
                drivers.add(driver);
            }
        }
        return drivers;
    }

    @Override
    public Driver getDriverById(Connection conn, int driverId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM driver WHERE driver_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Driver(
                        rs.getInt("driver_id"),
                        rs.getString("driver_name"),
                        rs.getString("driver_nic"),
                        rs.getString("driver_address"),
                        rs.getString("driver_email"),
                        rs.getString("license_image"),
                        rs.getString("driver_contact"),
                        rs.getString("status")
                );
            }
        }
        return null;
    }

    @Override
    public boolean updateDriver(Connection conn, Driver driver) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE driver SET driver_name = ?, driver_nic = ?, driver_address = ?, driver_email = ?, license_image = ?, driver_contact = ?, status = ? WHERE driver_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, driver.getDriverName());
            ps.setString(2, driver.getDriverNic());
            ps.setString(3, driver.getDriverAddress());
            ps.setString(4, driver.getDriverEmail());
            ps.setString(5, driver.getLicenseImage());
            ps.setString(6, driver.getDriverContact());
            ps.setString(7, driver.getStatus());
            ps.setInt(8, driver.getDriverId());

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    @Override
    public boolean deleteDriver(Connection conn, int driverId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM driver WHERE driver_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverId);

            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    @Override
    public List<Driver> getAvailableDrivers(Connection conn) throws Exception {
        String sql = "SELECT * FROM driver WHERE status = 'Available'";
        List<Driver> drivers = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Driver driver = new Driver();
                driver.setDriverId(rs.getInt("driver_id"));
                driver.setDriverName(rs.getString("driver_name"));
                driver.setDriverNic(rs.getString("driver_nic"));
                driver.setDriverAddress(rs.getString("driver_address"));
                driver.setDriverEmail(rs.getString("driver_email"));
                driver.setLicenseImage(rs.getString("license_image"));
                driver.setDriverContact(rs.getString("driver_contact"));
                driver.setStatus(rs.getString("status"));
                drivers.add(driver);
            }
        }
        return drivers;
    }
}