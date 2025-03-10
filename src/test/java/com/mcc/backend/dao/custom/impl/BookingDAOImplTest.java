package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.entity.Booking;
import com.mcc.backend.entity.Car;
import com.mcc.backend.entity.Driver;
import com.mcc.backend.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingDAOImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private BookingDAOImpl bookingDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
    }

    @Test
    void testSave() throws Exception {
        Booking booking = new Booking();
        booking.setUserId(1);
        booking.setCarId(1);
        booking.setDriverId(1);
        booking.setPickupLocation("Location A");
        booking.setDropLocation("Location B");
        booking.setBookingDateTime(new Timestamp(System.currentTimeMillis()));
        booking.setCustomerName("John Doe");
        booking.setCustomerEmail("john@example.com");
        booking.setCustomerPhone("1234567890");
        booking.setStatus("Pending");

        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        int bookingId = bookingDAO.save(connection, booking);

        assertEquals(1, bookingId);
        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).setInt(2, 1);
        verify(preparedStatement, times(1)).setInt(3, 1);
        verify(preparedStatement, times(1)).setString(4, "Location A");
        verify(preparedStatement, times(1)).setString(5, "Location B");
        verify(preparedStatement, times(1)).setTimestamp(6, booking.getBookingDateTime());
        verify(preparedStatement, times(1)).setString(7, "John Doe");
        verify(preparedStatement, times(1)).setString(8, "john@example.com");
        verify(preparedStatement, times(1)).setString(9, "1234567890");
        verify(preparedStatement, times(1)).setString(10, "Pending");
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetAllBookings() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Toyota Corolla");
        when(resultSet.getString("car_number")).thenReturn("ABC-1234");
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("driver_name")).thenReturn("Driver Name");
        when(resultSet.getString("driver_nic")).thenReturn("123456789V");

        List<Booking> bookings = bookingDAO.getAllBookings(connection);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("John Doe", bookings.get(0).getCustomerName());
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetBookingsByUserId() throws Exception {
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");

        List<Booking> bookings = bookingDAO.getBookingsByUserId(connection, userId);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("John Doe", bookings.get(0).getCustomerName());
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetTotalBookings() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("totalBookings")).thenReturn(10);

        int totalBookings = bookingDAO.getTotalBookings(connection);

        assertEquals(10, totalBookings);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetTotalRevenue() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getDouble("totalRevenue")).thenReturn(1000.0);

        double totalRevenue = bookingDAO.getTotalRevenue(connection);

        assertEquals(1000.0, totalRevenue);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetBookingCountsLast7Days() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("bookingDay")).thenReturn("2023-10-01");
        when(resultSet.getInt("bookingCount")).thenReturn(5);

        Map<String, Integer> bookingCounts = bookingDAO.getBookingCountsLast7Days(connection);

        assertNotNull(bookingCounts);
        assertEquals(1, bookingCounts.size());
        assertEquals(5, bookingCounts.get("2023-10-01"));
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetBookingsByStatus() throws Exception {
        String status = "Pending";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getString("car_name")).thenReturn("Toyota Corolla");
        when(resultSet.getString("car_number")).thenReturn("ABC-1234");
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("driver_name")).thenReturn("Driver Name");
        when(resultSet.getString("driver_nic")).thenReturn("123456789V");

        List<Booking> bookings = bookingDAO.getBookingsByStatus(connection, status);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("John Doe", bookings.get(0).getCustomerName());
        verify(preparedStatement, times(1)).setString(1, status);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUpdateBookingStatus() throws Exception {
        int bookingId = 1;
        String status = "Confirmed";

        when(preparedStatement.executeUpdate()).thenReturn(1);

        bookingDAO.updateBookingStatus(connection, bookingId, status);

        verify(preparedStatement, times(1)).setString(1, status);
        verify(preparedStatement, times(1)).setInt(2, bookingId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateBookingCarStatus() throws Exception {
        int carId = 1;
        String status = "Booked";

        when(preparedStatement.executeUpdate()).thenReturn(1);

        bookingDAO.updateBookingCarStatus(connection, carId, status);

        verify(preparedStatement, times(1)).setString(1, status);
        verify(preparedStatement, times(1)).setInt(2, carId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateBookingDriverStatus() throws Exception {
        int driverId = 1;
        String status = "Booked";

        when(preparedStatement.executeUpdate()).thenReturn(1);

        bookingDAO.updateBookingDriverStatus(connection, driverId, status);

        verify(preparedStatement, times(1)).setString(1, status);
        verify(preparedStatement, times(1)).setInt(2, driverId);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetBookingById() throws Exception {
        int bookingId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");

        Booking booking = bookingDAO.getBookingById(connection, bookingId);

        assertNotNull(booking);
        assertEquals("John Doe", booking.getCustomerName());
        verify(preparedStatement, times(1)).setInt(1, bookingId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetTotalBookingsByUserId() throws Exception {
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("total_bookings")).thenReturn(5);

        int totalBookings = bookingDAO.getTotalBookingsByUserId(connection, userId);

        assertEquals(5, totalBookings);
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetTotalSpendingByUserId() throws Exception {
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getDouble("total_spending")).thenReturn(500.0);

        double totalSpending = bookingDAO.getTotalSpendingByUserId(connection, userId);

        assertEquals(500.0, totalSpending);
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetActiveSinceByUserId() throws Exception {
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("active_since")).thenReturn("2023-10-01");

        String activeSince = bookingDAO.getActiveSinceByUserId(connection, userId);

        assertEquals("2023-10-01", activeSince);
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetFavoriteLocationByUserId() throws Exception {
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");

        String favoriteLocation = bookingDAO.getFavoriteLocationByUserId(connection, userId);

        assertEquals("Location A", favoriteLocation);
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetBookingsWithDetailsByUserId() throws Exception {
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getInt("car_id")).thenReturn(1);
        when(resultSet.getInt("driver_id")).thenReturn(1);
        when(resultSet.getString("pickup_location")).thenReturn("Location A");
        when(resultSet.getString("drop_location")).thenReturn("Location B");
        when(resultSet.getTimestamp("booking_datetime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("customer_name")).thenReturn("John Doe");
        when(resultSet.getString("customer_email")).thenReturn("john@example.com");
        when(resultSet.getString("customer_phone")).thenReturn("1234567890");
        when(resultSet.getString("status")).thenReturn("Pending");
        when(resultSet.getInt("payment_id")).thenReturn(1);
        when(resultSet.getDouble("amount")).thenReturn(100.0);
        when(resultSet.getString("payment_method")).thenReturn("Credit Card");
        when(resultSet.getString("payment_status")).thenReturn("Paid");
        when(resultSet.getTimestamp("payment_date")).thenReturn(new Timestamp(System.currentTimeMillis()));

        List<Booking> bookings = bookingDAO.getBookingsWithDetailsByUserId(connection, userId);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("John Doe", bookings.get(0).getCustomerName());
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }
}