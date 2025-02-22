package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.dao.custom.CarDAO;
import com.mcc.backend.dao.custom.impl.CarDAOImpl;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.entity.Car;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;
import com.mcc.backend.servlet.VehicleServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarBOImpl implements CarBO {

    private final CarDAO carDAO = new CarDAOImpl();

    @Override
    public boolean saveCar(CarDTO carDTO) throws SQLException, ClassNotFoundException {
        try (Connection connection = VehicleServlet.dataSource.getConnection()) {
            Car car = new Car();
            car.setCategoryId(carDTO.getCategoryId());
            car.setCarName(carDTO.getCarName());
            car.setCarNumber(carDTO.getCarNumber());
            car.setCarImage(carDTO.getCarImage());
            return carDAO.save(connection, car);
        }
    }

    @Override
    public List<CarDTO> getAllVehicles() throws SQLException, ClassNotFoundException {
        try (Connection connection = VehicleServlet.dataSource.getConnection()) {
            List<Car> cars = carDAO.findAll(connection);
            List<CarDTO> carDTOs = new ArrayList<>();

            for (Car car : cars) {
                CarDTO carDTO = new CarDTO();
                carDTO.setCarId(car.getCarId());
                carDTO.setCategoryId(car.getCategoryId());
                carDTO.setCarName(car.getCarName());
                carDTO.setCarNumber(car.getCarNumber());
                carDTO.setCarImage(car.getCarImage());
                carDTO.setStatus(car.getStatus());
                carDTOs.add(carDTO);
            }
            return carDTOs;
        }
    }

    @Override
    public boolean deleteCar(int carId) throws SQLException, ClassNotFoundException {
        try (Connection connection = VehicleServlet.dataSource.getConnection()) {
            return carDAO.delete(connection, carId);
        }
    }

    @Override
    public CarDTO getVehicleById(int carId) throws SQLException, ClassNotFoundException {
        try (Connection connection = VehicleServlet.dataSource.getConnection()) {
            Car car = carDAO.findById(connection, carId);
            if (car != null) {
                CarDTO carDTO = new CarDTO();
                carDTO.setCarId(car.getCarId());
                carDTO.setCategoryId(car.getCategoryId());
                carDTO.setCarName(car.getCarName());
                carDTO.setCarNumber(car.getCarNumber());
                carDTO.setCarImage(car.getCarImage());
                carDTO.setStatus(car.getStatus());
                return carDTO;
            }
            return null;
        }
    }

    @Override
    public boolean updateVehicle(CarDTO carDTO) throws SQLException, ClassNotFoundException {
        try (Connection connection = VehicleServlet.dataSource.getConnection()) {
            Car car = new Car();
            car.setCarId(carDTO.getCarId());
            car.setCategoryId(carDTO.getCategoryId());
            car.setCarName(carDTO.getCarName());
            car.setCarNumber(carDTO.getCarNumber());
            car.setCarImage(carDTO.getCarImage());
            car.setStatus(carDTO.getStatus());
            return carDAO.update(connection, car);
        }
    }

    @Override
    public List<CarDTO> getAvailableVehiclesByCategory(int categoryId) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            List<Car> cars = carDAO.getAvailableVehiclesByCategory(connection, categoryId);
            List<CarDTO> carDTOs = new ArrayList<>();

            for (Car car : cars) {
                CarDTO carDTO = new CarDTO();
                carDTO.setCarId(car.getCarId());
                carDTO.setCategoryId(car.getCategoryId());
                carDTO.setCarName(car.getCarName());
                carDTO.setCarNumber(car.getCarNumber());
                carDTO.setCarImage(car.getCarImage());
                carDTO.setStatus(car.getStatus());
                carDTOs.add(carDTO);
            }
            return carDTOs;
        }
    }

    @Override
    public int getAvailableVehicles() throws SQLException, ClassNotFoundException {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            return carDAO.getAvailableVehicles(connection);
        }
    }

    @Override
    public void updateCarStatus(CarDTO carDTO) throws Exception {
        try (Connection connection = StripeCheckoutServlet.dataSource.getConnection()) {
            Car car = new Car();
            car.setCarId(carDTO.getCarId());
            car.setStatus(carDTO.getStatus());
            carDAO.updateCarStatus(connection,car);
        }
    }
}