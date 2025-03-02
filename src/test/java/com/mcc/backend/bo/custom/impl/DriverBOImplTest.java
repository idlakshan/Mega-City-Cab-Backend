package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.dao.custom.DriverDAO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.entity.Driver;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.DriverServlet;
import com.mcc.backend.servlet.InvoiceServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DriverBOImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private DriverDAO driverDAO;

    private DriverBOImpl driverBO;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        driverBO = new DriverBOImpl();
        DriverServlet.dataSource = dataSource;
        BookingServlet.dataSource = dataSource;
        StripeCheckoutServlet.dataSource = dataSource;
        InvoiceServlet.dataSource = dataSource;
    }

    @Test
    public void testSaveDriver() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setDriverName("Thilan Samaraweera");
        driverDTO.setDriverNic("987656786V");
        driverDTO.setDriverAddress("Kottawa");
        driverDTO.setDriverEmail("samara@gmail.com");
        driverDTO.setDriverContact("0776787567");
        driverDTO.setLicenseImage("1739794898947_Driving-Licence.jpg");
        driverDTO.setStatus("Available");

        boolean isSaved = driverBO.saveDriver(driverDTO);
        assertTrue(isSaved);
    }

    @Test
    public void testGetAllDrivers() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);


        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("driver_id")).thenReturn(2, 3);
        when(resultSet.getString("driver_name")).thenReturn("Thilan Samaraweera", "Kasun Rajitha");
        when(resultSet.getString("driver_nic")).thenReturn("987656786V", "945645678V");
        when(resultSet.getString("driver_address")).thenReturn("Kottawa", "Panadura");
        when(resultSet.getString("driver_email")).thenReturn("samara@gmail.com", "Kasun@gmail.com");
        when(resultSet.getString("license_image")).thenReturn("1739794898947_Driving-Licence.jpg", "1739795454246_1739795445776_1739794931736_images (1).jpeg");
        when(resultSet.getString("driver_contact")).thenReturn("0776787567", "0786778345");
        when(resultSet.getString("status")).thenReturn("Available", "Available");

        List<DriverDTO> drivers = driverBO.getAllDrivers();
        assertNotNull(drivers);
        assertEquals(2, drivers.size());

        assertEquals(2, drivers.get(0).getDriverId());
        assertEquals("Thilan Samaraweera", drivers.get(0).getDriverName());
        assertEquals("987656786V", drivers.get(0).getDriverNic());
        assertEquals("Kottawa", drivers.get(0).getDriverAddress());
        assertEquals("samara@gmail.com", drivers.get(0).getDriverEmail());
        assertEquals("1739794898947_Driving-Licence.jpg", drivers.get(0).getLicenseImage());
        assertEquals("0776787567", drivers.get(0).getDriverContact());
        assertEquals("Available", drivers.get(0).getStatus());

        assertEquals(3, drivers.get(1).getDriverId());
        assertEquals("Kasun Rajitha", drivers.get(1).getDriverName());
        assertEquals("945645678V", drivers.get(1).getDriverNic());
        assertEquals("Panadura", drivers.get(1).getDriverAddress());
        assertEquals("Kasun@gmail.com", drivers.get(1).getDriverEmail());
        assertEquals("1739795454246_1739795445776_1739794931736_images (1).jpeg", drivers.get(1).getLicenseImage());
        assertEquals("0786778345", drivers.get(1).getDriverContact());
        assertEquals("Available", drivers.get(1).getStatus());
    }

    @Test
    public void testGetDriverById() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("driver_id")).thenReturn(2);
        when(resultSet.getString("driver_name")).thenReturn("Thilan Samaraweera");
        when(resultSet.getString("driver_nic")).thenReturn("987656786V");
        when(resultSet.getString("driver_address")).thenReturn("Kottawa");
        when(resultSet.getString("driver_email")).thenReturn("samara@gmail.com");
        when(resultSet.getString("license_image")).thenReturn("1739794898947_Driving-Licence.jpg");
        when(resultSet.getString("driver_contact")).thenReturn("0776787567");
        when(resultSet.getString("status")).thenReturn("Available");

        DriverDTO driverDTO = driverBO.getDriverById(2);
        assertNotNull(driverDTO);
        assertEquals("Thilan Samaraweera", driverDTO.getDriverName());
        assertEquals("987656786V", driverDTO.getDriverNic());
        assertEquals("Kottawa", driverDTO.getDriverAddress());
        assertEquals("samara@gmail.com", driverDTO.getDriverEmail());
        assertEquals("1739794898947_Driving-Licence.jpg", driverDTO.getLicenseImage());
        assertEquals("0776787567", driverDTO.getDriverContact());
        assertEquals("Available", driverDTO.getStatus());
    }

    @Test
    public void testUpdateDriver() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setDriverId(2);
        driverDTO.setDriverName("Thilan Samaraweera");
        driverDTO.setDriverNic("987656786V");
        driverDTO.setDriverAddress("Kottawa");
        driverDTO.setDriverEmail("samara@gmail.com");
        driverDTO.setDriverContact("0776787567");
        driverDTO.setLicenseImage("1739794898947_Driving-Licence.jpg");
        driverDTO.setStatus("Booked");

        boolean isUpdated = driverBO.updateDriver(driverDTO);
        assertTrue(isUpdated);
    }

    @Test
    public void testDeleteDriver() throws SQLException, ClassNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean isDeleted = driverBO.deleteDriver(2);
        assertTrue(isDeleted);
    }

    @Test
    public void testGetAvailableDrivers() throws Exception {
        when(BookingServlet.dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);


        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("driver_id")).thenReturn(2, 3);
        when(resultSet.getString("driver_name")).thenReturn("Thilan Samaraweera", "Kasun Rajitha");
        when(resultSet.getString("driver_nic")).thenReturn("987656786V", "945645678V");
        when(resultSet.getString("driver_address")).thenReturn("Kottawa", "Panadura");
        when(resultSet.getString("driver_email")).thenReturn("samara@gmail.com", "Kasun@gmail.com");
        when(resultSet.getString("license_image")).thenReturn("1739794898947_Driving-Licence.jpg", "1739795454246_1739795445776_1739794931736_images (1).jpeg");
        when(resultSet.getString("driver_contact")).thenReturn("0776787567", "0786778345");
        when(resultSet.getString("status")).thenReturn("Available", "Available");

        List<DriverDTO> drivers = driverBO.getAvailableDrivers();
        assertNotNull(drivers);
        assertEquals(2, drivers.size());


        assertEquals(2, drivers.get(0).getDriverId());
        assertEquals("Thilan Samaraweera", drivers.get(0).getDriverName());
        assertEquals("Available", drivers.get(0).getStatus());


        assertEquals(3, drivers.get(1).getDriverId());
        assertEquals("Kasun Rajitha", drivers.get(1).getDriverName());
        assertEquals("Available", drivers.get(1).getStatus());
    }

    @Test
    public void testGetActiveDrivers() throws SQLException, ClassNotFoundException {
        when(BookingServlet.dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("activeDrivers")).thenReturn(2);

        int activeDrivers = driverBO.getActiveDrivers();
        assertEquals(2, activeDrivers);
    }

    @Test
    public void testUpdateDriverStatus() throws Exception {
        when(StripeCheckoutServlet.dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setDriverId(2);
        driverDTO.setStatus("Booked");

        driverBO.updateDriverStatus(driverDTO);

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetBookedDriverById() throws Exception {
        when(InvoiceServlet.dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("driver_id")).thenReturn(2);
        when(resultSet.getString("driver_name")).thenReturn("Thilan Samaraweera");
        when(resultSet.getString("driver_nic")).thenReturn("987656786V");
        when(resultSet.getString("driver_address")).thenReturn("Kottawa");
        when(resultSet.getString("driver_email")).thenReturn("samara@gmail.com");
        when(resultSet.getString("license_image")).thenReturn("1739794898947_Driving-Licence.jpg");
        when(resultSet.getString("driver_contact")).thenReturn("0776787567");
        when(resultSet.getString("status")).thenReturn("Booked");

        DriverDTO driverDTO = driverBO.getBookedDriverById(2);
        assertNotNull(driverDTO);
        assertEquals("Thilan Samaraweera", driverDTO.getDriverName());
        assertEquals("Booked", driverDTO.getStatus());
    }
}