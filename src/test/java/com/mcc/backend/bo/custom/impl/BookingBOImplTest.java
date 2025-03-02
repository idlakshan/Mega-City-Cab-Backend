package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.dto.PaymentDTO;
import com.mcc.backend.entity.Booking;
import com.mcc.backend.entity.Car;
import com.mcc.backend.entity.Driver;
import com.mcc.backend.entity.Payment;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingBOImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private BookingDAO bookingDAO;

    private BookingBOImpl bookingBO;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        bookingBO = new BookingBOImpl();
        BookingServlet.dataSource = dataSource;
        StripeCheckoutServlet.dataSource = dataSource;
    }

    @Test
    public void testSaveBooking() throws Exception {
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(1);
        bookingDTO.setCarId(1);
        bookingDTO.setDriverId(1);
        bookingDTO.setPickupLocation("Location A");
        bookingDTO.setDropLocation("Location B");
        bookingDTO.setBookingDateTime(new Timestamp(System.currentTimeMillis()));
        bookingDTO.setCustomerName("John Doe");
        bookingDTO.setCustomerEmail("john.doe@example.com");
        bookingDTO.setCustomerPhone("1234567890");
        bookingDTO.setStatus("Pending");

        int bookingId = bookingBO.saveBooking(bookingDTO);
        assertEquals(1, bookingId);
    }

    @Test
    public void testGetAllBookings() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false); // Two rows
        when(resultSet.getInt("booking_id")).thenReturn(1, 2);
        when(resultSet.getInt("user_id")).thenReturn(1, 2);
        when(resultSet.getString("pickup_location")).thenReturn("Location A", "Location B");
        when(resultSet.getString("drop_location")).thenReturn("Location C", "Location D");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe", "Jane Doe");
        when(resultSet.getString("customer_email")).thenReturn("john.doe@example.com", "jane.doe@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890", "0987654321");
        when(resultSet.getString("status")).thenReturn("Pending", "Completed");

        when(resultSet.getInt("car_id")).thenReturn(1, 2);
        when(resultSet.getString("car_name")).thenReturn("Toyota", "Honda");
        when(resultSet.getString("car_number")).thenReturn("ABC-123", "XYZ-456");

        when(resultSet.getInt("driver_id")).thenReturn(1, 2);
        when(resultSet.getString("driver_name")).thenReturn("Driver A", "Driver B");
        when(resultSet.getString("driver_nic")).thenReturn("123456789V", "987654321V");

        List<BookingDTO> bookings = bookingBO.getAllBookings();
        assertNotNull(bookings);
        assertEquals(2, bookings.size());

        assertEquals(1, bookings.get(0).getBookingId());
        assertEquals("Location A", bookings.get(0).getPickupLocation());
        assertEquals("Toyota", bookings.get(0).getCar().getCarName());

        assertEquals(2, bookings.get(1).getBookingId());
        assertEquals("Location B", bookings.get(1).getPickupLocation());
        assertEquals("Honda", bookings.get(1).getCar().getCarName());
    }

    @Test
    public void testGetBookingsByUserId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false); // One row
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john.doe@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");

        List<BookingDTO> bookings = bookingBO.getBookingsByUserId(1);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());

        assertEquals(1, bookings.get(0).getBookingId());
        assertEquals("Location A", bookings.get(0).getPickupLocation());
        assertEquals("Pending", bookings.get(0).getStatus());
    }

    @Test
    public void testGetTotalBookings() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("totalBookings")).thenReturn(10);

        int totalBookings = bookingBO.getTotalBookings();
        assertEquals(10, totalBookings);
    }

    @Test
    public void testGetTotalRevenue() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getDouble("totalRevenue")).thenReturn(1000.0);

        double totalRevenue = bookingBO.getTotalRevenue();
        assertEquals(1000.0, totalRevenue);
    }

    @Test
    public void testGetBookingCountsLast7Days() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false); // Two rows
        when(resultSet.getString("bookingDay")).thenReturn("2023-10-01", "2023-10-02");
        when(resultSet.getInt("bookingCount")).thenReturn(5, 3);

        Map<String, Integer> bookingCounts = bookingBO.getBookingCountsLast7Days();
        assertNotNull(bookingCounts);
        assertEquals(2, bookingCounts.size());
        assertEquals(5, bookingCounts.get("2023-10-01"));
        assertEquals(3, bookingCounts.get("2023-10-02"));
    }

    @Test
    public void testGetBookingsByStatus() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false); // One row
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john.doe@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");

        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Toyota");
        when(resultSet.getString("car_number")).thenReturn("ABC-123");

        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("driver_name")).thenReturn("Driver A");
        when(resultSet.getString("driver_nic")).thenReturn("123456789V");

        List<BookingDTO> bookings = bookingBO.getBookingsByStatus("Pending");
        assertNotNull(bookings);
        assertEquals(1, bookings.size());

        assertEquals(1, bookings.get(0).getBookingId());
        assertEquals("Location A", bookings.get(0).getPickupLocation());
        assertEquals("Pending", bookings.get(0).getStatus());
    }

    @Test
    public void testUpdateBookingStatus() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        bookingBO.updateBookingStatus(1, "Completed");
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testUpdateBookingCarStatus() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        bookingBO.updateBookingCarStatus(1, "Available");
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testUpdateBookingDriverStatus() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        bookingBO.updateBookingDriverStatus(1, "Available");
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetBookingById() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john.doe@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");

        BookingDTO booking = bookingBO.getBookingById(1);
        assertNotNull(booking);
        assertEquals(1, booking.getBookingId());
        assertEquals("Location A", booking.getPickupLocation());
        assertEquals("Pending", booking.getStatus());
    }

    @Test
    public void testGetTotalBookingsByUserId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("total_bookings")).thenReturn(5);

        int totalBookings = bookingBO.getTotalBookingsByUserId(1);
        assertEquals(5, totalBookings);
    }

    @Test
    public void testGetTotalSpendingByUserId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getDouble("total_spending")).thenReturn(500.0);

        double totalSpending = bookingBO.getTotalSpendingByUserId(1);
        assertEquals(500.0, totalSpending);
    }

    @Test
    public void testGetActiveSinceByUserId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("active_since")).thenReturn("2023-01-01");

        String activeSince = bookingBO.getActiveSinceByUserId(1);
        assertEquals("2023-01-01", activeSince);
    }

    @Test
    public void testGetFavoriteLocationByUserId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");

        String favoriteLocation = bookingBO.getFavoriteLocationByUserId(1);
        assertEquals("Location A", favoriteLocation);
    }

    @Test
    public void testGetBookingsDetailsByUserId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false); // One row
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john.doe@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");

        when(resultSet.getInt("payment_id")).thenReturn(1);
        when(resultSet.getDouble("amount")).thenReturn(100.0);
        when(resultSet.getString("payment_method")).thenReturn("Credit Card");
        when(resultSet.getString("payment_status")).thenReturn("Paid");
        when(resultSet.getTimestamp("payment_date")).thenReturn(new Timestamp(System.currentTimeMillis()));

        List<BookingDTO> bookings = bookingBO.getBookingsDetailsByUserId(1);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());

        assertEquals(1, bookings.get(0).getBookingId());
        assertEquals("Location A", bookings.get(0).getPickupLocation());
        assertEquals("Pending", bookings.get(0).getStatus());

        assertEquals(1, bookings.get(0).getPayment().getPaymentId());
        assertEquals(100.0, bookings.get(0).getPayment().getAmount());
        assertEquals("Credit Card", bookings.get(0).getPayment().getPaymentMethod());
    }
}