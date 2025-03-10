package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.entity.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarDAOImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private CarDAOImpl carDAO;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testFindById() throws SQLException {
        int carId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Toyota Corolla");
        when(resultSet.getString("car_number")).thenReturn("ABC-1234");
        when(resultSet.getString("car_image")).thenReturn("image.jpg");
        when(resultSet.getString("status")).thenReturn("Available");

        Car car = carDAO.findById(connection, carId);

        assertNotNull(car);
        assertEquals("Toyota Corolla", car.getCarName());
        verify(preparedStatement, times(1)).setInt(1, carId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testFindAll() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Toyota Corolla");
        when(resultSet.getString("car_number")).thenReturn("ABC-1234");
        when(resultSet.getString("car_image")).thenReturn("image.jpg");
        when(resultSet.getString("status")).thenReturn("Available");

        List<Car> cars = carDAO.findAll(connection);

        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals("Toyota Corolla", cars.get(0).getCarName());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testSave() throws SQLException {
        Car car = new Car();
        car.setCategoryId(1);
        car.setCarName("Toyota Corolla");
        car.setCarNumber("ABC-1234");
        car.setCarImage("image.jpg");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = carDAO.save(connection, car);

        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).setString(2, "Toyota Corolla");
        verify(preparedStatement, times(1)).setString(3, "ABC-1234");
        verify(preparedStatement, times(1)).setString(4, "image.jpg");
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdate() throws SQLException {
        Car car = new Car();
        car.setCarId(1);
        car.setCategoryId(1);
        car.setCarName("Toyota Corolla");
        car.setCarNumber("ABC-1234");
        car.setCarImage("image.jpg");
        car.setStatus("Available");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = carDAO.update(connection, car);

        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).setString(2, "Toyota Corolla");
        verify(preparedStatement, times(1)).setString(3, "ABC-1234");
        verify(preparedStatement, times(1)).setString(4, "image.jpg");
        verify(preparedStatement, times(1)).setString(5, "Available");
        verify(preparedStatement, times(1)).setInt(6, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDelete() throws SQLException {
        int carId = 1;
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = carDAO.delete(connection, carId);

        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, carId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testIsCarNumberExists() throws SQLException {
        String carNumber = "ABC-1234";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        boolean result = carDAO.isCarNumberExists(connection, carNumber);

        assertTrue(result);
        verify(preparedStatement, times(1)).setString(1, carNumber);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetAvailableVehiclesByCategory() throws Exception {
        int categoryId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Toyota Corolla");
        when(resultSet.getString("car_number")).thenReturn("ABC-1234");
        when(resultSet.getString("car_image")).thenReturn("image.jpg");
        when(resultSet.getString("status")).thenReturn("Available");

        List<Car> cars = carDAO.getAvailableVehiclesByCategory(connection, categoryId);

        assertNotNull(cars);
        assertEquals(1, cars.size());
        assertEquals("Toyota Corolla", cars.get(0).getCarName());
        verify(preparedStatement, times(1)).setInt(1, categoryId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetAvailableVehicles() throws SQLException, ClassNotFoundException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("availableVehicles")).thenReturn(5);

        int result = carDAO.getAvailableVehicles(connection);

        assertEquals(5, result);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateCarStatus() throws Exception {
        Car car = new Car();
        car.setCarId(1);
        car.setStatus("Booked");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        carDAO.updateCarStatus(connection, car);

        verify(preparedStatement, times(1)).setString(1, "Booked");
        verify(preparedStatement, times(1)).setInt(2, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetBookedVehicleById() throws Exception {
        int carId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Toyota Corolla");
        when(resultSet.getString("car_number")).thenReturn("ABC-1234");
        when(resultSet.getString("car_image")).thenReturn("image.jpg");
        when(resultSet.getString("status")).thenReturn("Booked");

        Car car = carDAO.getBookedVehicleById(connection, carId);

        assertNotNull(car);
        assertEquals("Toyota Corolla", car.getCarName());
        verify(preparedStatement, times(1)).setInt(1, carId);
        verify(preparedStatement, times(1)).executeQuery();
    }
}