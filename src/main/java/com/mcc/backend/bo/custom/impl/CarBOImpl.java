package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.dao.custom.CarDAO;
import com.mcc.backend.dao.custom.impl.CarDAOImpl;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.entity.Car;

import java.sql.SQLException;

public class CarBOImpl implements CarBO {

    private final CarDAO carDAO = new CarDAOImpl();

    @Override
    public void saveCar(CarDTO dto) throws SQLException, ClassNotFoundException {
        if (carDAO.isCarNumberExists(dto.getCarNumber())) {
            throw new SQLException("Car number already exists");
        }

        Car car = new Car();
        car.setCategoryId(dto.getCategoryId());
        car.setCarName(dto.getCarName());
        car.setCarNumber(dto.getCarNumber());
        car.setCarImage(dto.getCarImage());

        carDAO.saveCar(car);
    }
}