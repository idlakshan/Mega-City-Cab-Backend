package com.mcc.backend.dao.custom;

import com.mcc.backend.entity.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DriverDAO {
    boolean saveDriver(Connection conn, Driver driver) throws SQLException, ClassNotFoundException;
    List<Driver> getAllDrivers(Connection conn) throws SQLException, ClassNotFoundException;
    Driver getDriverById(Connection conn, int driverId) throws SQLException, ClassNotFoundException;
}