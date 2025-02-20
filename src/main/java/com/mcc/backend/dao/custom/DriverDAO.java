package com.mcc.backend.dao.custom;

import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.entity.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DriverDAO {
    boolean saveDriver(Connection conn, Driver driver) throws SQLException, ClassNotFoundException;
    List<Driver> getAllDrivers(Connection conn) throws SQLException, ClassNotFoundException;
    Driver getDriverById(Connection conn, int driverId) throws SQLException, ClassNotFoundException;
    boolean updateDriver(Connection conn, Driver driver) throws SQLException, ClassNotFoundException;
    boolean deleteDriver(Connection conn, int driverId) throws SQLException, ClassNotFoundException;
    List<Driver> getAvailableDrivers(Connection conn) throws Exception;
    int getActiveDrivers(Connection conn) throws SQLException, ClassNotFoundException;
    void updateDriverStatus(DriverDTO driverDTO) throws Exception;
}