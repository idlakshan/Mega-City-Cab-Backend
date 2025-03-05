package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.entity.Driver;
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

class DriverDAOImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DriverDAOImpl driverDAO;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testFindById() throws SQLException {
        int driverId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("driver_name")).thenReturn("John Doe");
        when(resultSet.getString("driver_nic")).thenReturn("123456789V");
        when(resultSet.getString("driver_address")).thenReturn("123 Main St");
        when(resultSet.getString("driver_email")).thenReturn("john@example.com");
        when(resultSet.getString("license_image")).thenReturn("license.jpg");
        when(resultSet.getString("driver_contact")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Active");

        Driver driver = driverDAO.findById(connection, driverId);

        assertNotNull(driver);
        assertEquals("John Doe", driver.getDriverName());
        verify(preparedStatement, times(1)).setInt(1, driverId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testFindAll() throws SQLException {
        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("driver_name")).thenReturn("John Doe");
        when(resultSet.getString("driver_nic")).thenReturn("123456789V");
        when(resultSet.getString("driver_address")).thenReturn("123 Main St");
        when(resultSet.getString("driver_email")).thenReturn("john@example.com");
        when(resultSet.getString("license_image")).thenReturn("license.jpg");
        when(resultSet.getString("driver_contact")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Active");

        List<Driver> drivers = driverDAO.findAll(connection);

        assertNotNull(drivers);
        assertEquals(1, drivers.size());
        assertEquals("John Doe", drivers.get(0).getDriverName());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testSave() throws SQLException {
        Driver driver = new Driver();
        driver.setDriverName("John Doe");
        driver.setDriverNic("123456789V");
        driver.setDriverAddress("123 Main St");
        driver.setDriverEmail("john@example.com");
        driver.setLicenseImage("license.jpg");
        driver.setDriverContact("1234567890");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = driverDAO.save(connection, driver);

        assertTrue(result);
        verify(preparedStatement, times(1)).setString(1, "John Doe");
        verify(preparedStatement, times(1)).setString(2, "123456789V");
        verify(preparedStatement, times(1)).setString(3, "123 Main St");
        verify(preparedStatement, times(1)).setString(4, "john@example.com");
        verify(preparedStatement, times(1)).setString(5, "license.jpg");
        verify(preparedStatement, times(1)).setString(6, "1234567890");
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdate() throws SQLException {
        Driver driver = new Driver();
        driver.setDriverId(1);
        driver.setDriverName("John Doe");
        driver.setDriverNic("123456789V");
        driver.setDriverAddress("123 Main St");
        driver.setDriverEmail("john@example.com");
        driver.setLicenseImage("license.jpg");
        driver.setDriverContact("1234567890");
        driver.setStatus("Active");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = driverDAO.update(connection, driver);

        assertTrue(result);
        verify(preparedStatement, times(1)).setString(1, "John Doe");
        verify(preparedStatement, times(1)).setString(2, "123456789V");
        verify(preparedStatement, times(1)).setString(3, "123 Main St");
        verify(preparedStatement, times(1)).setString(4, "john@example.com");
        verify(preparedStatement, times(1)).setString(5, "license.jpg");
        verify(preparedStatement, times(1)).setString(6, "1234567890");
        verify(preparedStatement, times(1)).setString(7, "Active");
        verify(preparedStatement, times(1)).setInt(8, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDelete() throws SQLException {
        int driverId = 1;
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean result = driverDAO.delete(connection, driverId);

        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, driverId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetAvailableDrivers() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false); // Simulate one row in the result set
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("driver_name")).thenReturn("John Doe");
        when(resultSet.getString("driver_nic")).thenReturn("123456789V");
        when(resultSet.getString("driver_address")).thenReturn("123 Main St");
        when(resultSet.getString("driver_email")).thenReturn("john@example.com");
        when(resultSet.getString("license_image")).thenReturn("license.jpg");
        when(resultSet.getString("driver_contact")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Available");

        List<Driver> drivers = driverDAO.getAvailableDrivers(connection);

        assertNotNull(drivers);
        assertEquals(1, drivers.size());
        assertEquals("John Doe", drivers.get(0).getDriverName());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetActiveDrivers() throws SQLException, ClassNotFoundException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("activeDrivers")).thenReturn(5);


        int result = driverDAO.getActiveDrivers(connection);


        assertEquals(5, result);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateDriverStatus() throws Exception {

        Driver driver = new Driver();
        driver.setDriverId(1);
        driver.setStatus("Inactive");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        driverDAO.updateDriverStatus(connection, driver);

        verify(preparedStatement, times(1)).setString(1, "Inactive");
        verify(preparedStatement, times(1)).setInt(2, 1);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetBookedDriverById() throws Exception {
        int driverId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("driver_name")).thenReturn("John Doe");
        when(resultSet.getString("driver_nic")).thenReturn("123456789V");
        when(resultSet.getString("driver_address")).thenReturn("123 Main St");
        when(resultSet.getString("driver_email")).thenReturn("john@example.com");
        when(resultSet.getString("license_image")).thenReturn("license.jpg");
        when(resultSet.getString("driver_contact")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Booked");

        Driver driver = driverDAO.getBookedDriverById(connection, driverId);

        assertNotNull(driver);
        assertEquals("John Doe", driver.getDriverName());
        verify(preparedStatement, times(1)).setInt(1, driverId);
        verify(preparedStatement, times(1)).executeQuery();
    }
}