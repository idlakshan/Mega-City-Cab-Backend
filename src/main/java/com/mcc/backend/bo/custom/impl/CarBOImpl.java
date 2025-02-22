package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.dao.custom.CarDAO;
import com.mcc.backend.dao.custom.impl.CarDAOImpl;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.entity.Car;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.VehicleServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarBOImpl implements CarBO {

    private final CarDAO carDAO = new CarDAOImpl();

    @Override
    public boolean saveCar(CarDTO dto) throws SQLException, ClassNotFoundException {
        try (Connection conn = VehicleServlet.dataSource.getConnection()) {
            if (carDAO.isCarNumberExists(conn, dto.getCarNumber())) {
                throw new SQLException("Car number already exists");
            }

            Car car = new Car();
            car.setCategoryId(dto.getCategoryId());
            car.setCarName(dto.getCarName());
            car.setCarNumber(dto.getCarNumber());
            car.setCarImage(dto.getCarImage());

            return carDAO.saveCar(conn, car);
        }
    }

    @Override
    public List<CarDTO> getAllVehicles() throws SQLException, ClassNotFoundException {
        try (Connection conn = VehicleServlet.dataSource.getConnection()) {
            List<Car> cars = carDAO.getAllVehicles(conn);
            List<CarDTO> carDTOs = new ArrayList<>();

            for (Car car : cars) {
                CarDTO dto = new CarDTO();
                dto.setCarId(car.getCarId());
                dto.setCategoryId(car.getCategoryId());
                dto.setCarName(car.getCarName());
                dto.setCarNumber(car.getCarNumber());
                dto.setCarImage(car.getCarImage());
                dto.setStatus(car.getStatus());
                carDTOs.add(dto);
            }

            return carDTOs;
        }
    }

    @Override
    public boolean deleteCar(int carId) throws SQLException, ClassNotFoundException {
        try (Connection conn = VehicleServlet.dataSource.getConnection()) {
            return carDAO.deleteCar(conn, carId);
        }
    }

    @Override
    public CarDTO getVehicleById(int carId) throws SQLException, ClassNotFoundException {
        try (Connection conn = VehicleServlet.dataSource.getConnection()) {
            Car car = carDAO.getVehicleById(conn, carId);

            if (car != null) {
                CarDTO dto = new CarDTO();
                dto.setCarId(car.getCarId());
                dto.setCategoryId(car.getCategoryId());
                dto.setCarName(car.getCarName());
                dto.setCarNumber(car.getCarNumber());
                dto.setCarImage(car.getCarImage());
                dto.setStatus(car.getStatus());
                return dto;
            }
            return null;
        }
    }

    @Override
    public boolean updateVehicle(CarDTO dto) throws SQLException, ClassNotFoundException {
        try (Connection conn = VehicleServlet.dataSource.getConnection()) {
            Car car = new Car();
            car.setCategoryId(dto.getCategoryId());
            car.setCarName(dto.getCarName());
            car.setCarNumber(dto.getCarNumber());
            car.setCarImage(dto.getCarImage());
            car.setStatus(dto.getStatus());
            car.setCarId(dto.getCarId());

            return carDAO.updateVehicle(conn, car);
        }
    }

    @Override
    public List<CarDTO> getAvailableVehiclesByCategory(int categoryId) throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            List<Car> availableVehiclesByCategory = carDAO.getAvailableVehiclesByCategory(conn, categoryId);
            List<CarDTO> carDTOs = new ArrayList<>();
            for (Car car : availableVehiclesByCategory) {
                CarDTO dto = new CarDTO();
                dto.setCarId(car.getCarId());
                dto.setCategoryId(car.getCategoryId());
                dto.setCarName(car.getCarName());
                dto.setCarNumber(car.getCarNumber());
                dto.setCarImage(car.getCarImage());
                dto.setStatus(car.getStatus());
                carDTOs.add(dto);
            }
            return carDTOs;
        }
    }

    @Override
    public int getAvailableVehicles() throws SQLException, ClassNotFoundException {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return carDAO.getAvailableVehicles(conn);
        }
    }

    @Override
    public void updateCarStatus(CarDTO carDTO) throws Exception {
        Car car = new Car();
        car.setCarId(carDTO.getCarId());
        car.setStatus(carDTO.getStatus());
        carDAO.updateCarStatus(car);
    }




}