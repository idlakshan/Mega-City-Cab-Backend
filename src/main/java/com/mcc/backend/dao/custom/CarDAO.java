package com.mcc.backend.dao.custom;

import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.entity.Car;

import java.sql.SQLException;
import java.util.List;

public interface CarDAO {
    boolean isCarNumberExists(String carNumber) throws SQLException;
    void saveCar(Car car) throws SQLException;
    List<CarDTO> getAllVehicles() throws SQLException, ClassNotFoundException;
    boolean deleteCar(int carId) throws SQLException, ClassNotFoundException;
    CarDTO getVehicleById(int carId) throws SQLException, ClassNotFoundException;
    boolean updateVehicle(Car car)throws SQLException, ClassNotFoundException;
}
