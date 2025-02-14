package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.entity.Car;

import java.sql.SQLException;
import java.util.List;

public interface CarBO {

   void saveCar(CarDTO car) throws SQLException, ClassNotFoundException;
   List<CarDTO> getAllVehicles() throws SQLException, ClassNotFoundException;

}
