package com.mcc.backend.dao.custom;

import com.mcc.backend.dao.CrudDAO;
import com.mcc.backend.entity.Car;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CarDAO extends CrudDAO<Car, Integer> {
    boolean isCarNumberExists(Connection conn, String carNumber) throws SQLException;
    List<Car> getAvailableVehiclesByCategory(Connection conn, int categoryId) throws Exception;
    int getAvailableVehicles(Connection conn) throws SQLException, ClassNotFoundException;
    void updateCarStatus(Connection conn,Car car) throws Exception;
}