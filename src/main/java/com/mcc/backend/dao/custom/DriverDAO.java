package com.mcc.backend.dao.custom;

import com.mcc.backend.dao.CrudDAO;
import com.mcc.backend.entity.Driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DriverDAO extends CrudDAO<Driver, Integer> {
    List<Driver> getAvailableDrivers(Connection conn) throws Exception;
    int getActiveDrivers(Connection conn) throws SQLException, ClassNotFoundException;
    void updateDriverStatus(Connection conn,Driver driver) throws Exception;
}