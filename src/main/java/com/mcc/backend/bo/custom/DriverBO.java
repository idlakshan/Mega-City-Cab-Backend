package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.DriverDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DriverBO {
    boolean saveDriver(DriverDTO driver) throws SQLException, ClassNotFoundException;
    List<DriverDTO> getAllDrivers() throws SQLException, ClassNotFoundException;
    DriverDTO getDriverById(int driverId) throws SQLException, ClassNotFoundException;
    boolean updateDriver(DriverDTO driver) throws SQLException, ClassNotFoundException;
    boolean deleteDriver(int driverId) throws SQLException, ClassNotFoundException;
    List<DriverDTO> getAvailableDrivers() throws Exception;
    int getActiveDrivers() throws SQLException, ClassNotFoundException;
    void updateDriverStatus(DriverDTO driverDTO) throws Exception;

}