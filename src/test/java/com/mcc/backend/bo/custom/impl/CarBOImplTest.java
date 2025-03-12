package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.InvoiceServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;
import com.mcc.backend.servlet.VehicleServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarBOImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private CarBOImpl carBO;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        carBO = new CarBOImpl();
        VehicleServlet.dataSource = dataSource;
        BookingServlet.dataSource = dataSource;
        StripeCheckoutServlet.dataSource = dataSource;
        InvoiceServlet.dataSource = dataSource;
    }

    @Test
    public void testGetAllVehicles() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Toyota Test");
        when(resultSet.getString("car_number")).thenReturn("Western CAF-2213");
        when(resultSet.getString("car_image")).thenReturn("image.jpg");
        when(resultSet.getString("status")).thenReturn("Available");

        List<CarDTO> vehicles = carBO.getAllVehicles();
        assertNotNull(vehicles);
        assertEquals(1, vehicles.size());
    }

    @Test
    public void testSaveCar() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        CarDTO carDTO = new CarDTO();
        carDTO.setCategoryId(1);
        carDTO.setCarName("Test Car");
        carDTO.setCarNumber("1234");
        carDTO.setCarImage("image.jpg");

        boolean isSaved = carBO.saveCar(carDTO);
        assertTrue(isSaved);
    }

    @Test
    public void testDeleteCar() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean isDeleted = carBO.deleteCar(1);
        assertTrue(isDeleted);
    }

    @Test
    public void testGetVehicleById_InvalidId() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        CarDTO carDTO = carBO.getVehicleById(999);
        assertNull(carDTO);
    }

    @Test
    public void testGetVehicleById() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Test Car");
        when(resultSet.getString("car_number")).thenReturn("1234");
        when(resultSet.getString("car_image")).thenReturn("image.jpg");
        when(resultSet.getString("status")).thenReturn("Available");

        CarDTO carDTO = carBO.getVehicleById(1);
        assertNotNull(carDTO);
        assertEquals(1, carDTO.getCarId());
        assertEquals("Test Car", carDTO.getCarName());
    }

    @Test
    public void testUpdateVehicle() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        CarDTO carDTO = new CarDTO();
        carDTO.setCarId(1);
        carDTO.setCategoryId(1);
        carDTO.setCarName("Updated Car");
        carDTO.setCarNumber("5678");
        carDTO.setCarImage("updated_image.jpg");
        carDTO.setStatus("Booked");

        boolean isUpdated = carBO.updateVehicle(carDTO);
        assertTrue(isUpdated);
    }

    @Test
    public void testGetAvailableVehiclesByCategory() throws Exception {
        when(BookingServlet.dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Test Car");
        when(resultSet.getString("car_number")).thenReturn("1234");
        when(resultSet.getString("car_image")).thenReturn("image.jpg");
        when(resultSet.getString("status")).thenReturn("Available");

        List<CarDTO> carDTOs = carBO.getAvailableVehiclesByCategory(1);

        assertNotNull(carDTOs);
        assertEquals(1, carDTOs.size());
        assertEquals("Test Car", carDTOs.get(0).getCarName());
    }

    @Test
    public void testUpdateCarStatus() throws Exception {
        when(StripeCheckoutServlet.dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        CarDTO carDTO = new CarDTO();
        carDTO.setCarId(1);
        carDTO.setStatus("Booked");

        carBO.updateCarStatus(carDTO);

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetBookedVehicleById() throws Exception {
        when(InvoiceServlet.dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);


        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("category_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Booked Car");
        when(resultSet.getString("car_number")).thenReturn("5678");
        when(resultSet.getString("car_image")).thenReturn("booked_image.jpg");
        when(resultSet.getString("status")).thenReturn("Booked");


        CarDTO carDTO = carBO.getBookedVehicleById(1);

        assertNotNull(carDTO);
        assertEquals("Booked Car", carDTO.getCarName());
        assertEquals("Booked", carDTO.getStatus());
    }
}