package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.entity.Booking;

import java.sql.*;
import java.util.*;

public class BookingDAOImpl implements BookingDAO {

    @Override
    public int save(Connection connection, Booking booking) throws Exception {
        String sql = "INSERT INTO booking (user_id, car_id, driver_id, pickup_location, drop_location, booking_datetime, customer_name, customer_email, customer_phone, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setInt(1, booking.getUserId());
            pstm.setInt(2, booking.getCarId());
            pstm.setInt(3, booking.getDriverId());
            pstm.setString(4, booking.getPickupLocation());
            pstm.setString(5, booking.getDropLocation());
            pstm.setTimestamp(6, booking.getBookingDateTime());
            pstm.setString(7, booking.getCustomerName());
            pstm.setString(8, booking.getCustomerEmail());
            pstm.setString(9, booking.getCustomerPhone());
            pstm.setString(10, booking.getStatus());

            pstm.executeUpdate();

            try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new Exception("Creating booking failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public List<BookingDTO> getAllBookings(Connection connection) throws Exception {
        List<BookingDTO> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking";
        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                BookingDTO dto = new BookingDTO();
                dto.setBookingId(rs.getInt("booking_id"));
                dto.setUserId(rs.getInt("user_id"));
                dto.setCarId(rs.getInt("car_id"));
                dto.setDriverId(rs.getInt("driver_id"));
                dto.setPickupLocation(rs.getString("pickup_location"));
                dto.setDropLocation(rs.getString("drop_location"));
                dto.setBookingDateTime(rs.getTimestamp("booking_datetime"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setCustomerEmail(rs.getString("customer_email"));
                dto.setCustomerPhone(rs.getString("customer_phone"));
                dto.setStatus(rs.getString("status"));

                bookings.add(dto);
            }
        }
        return bookings;
    }

    @Override
    public List<BookingDTO> getBookingsByUserId(Connection connection, int userId) throws Exception {
        List<BookingDTO> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE user_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, userId);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    BookingDTO dto = new BookingDTO();
                    dto.setBookingId(rs.getInt("booking_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setCarId(rs.getInt("car_id"));
                    dto.setDriverId(rs.getInt("driver_id"));
                    dto.setPickupLocation(rs.getString("pickup_location"));
                    dto.setDropLocation(rs.getString("drop_location"));
                    dto.setBookingDateTime(rs.getTimestamp("booking_datetime"));
                    dto.setCustomerName(rs.getString("customer_name"));
                    dto.setCustomerEmail(rs.getString("customer_email"));
                    dto.setCustomerPhone(rs.getString("customer_phone"));
                    dto.setStatus(rs.getString("status"));
                    bookings.add(dto);
                }
            }
        }
        return bookings;
    }

    @Override
    public int getTotalBookings(Connection conn) {
        String sql = "SELECT COUNT(*) AS totalBookings FROM Booking";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalBookings");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getTotalRevenue(Connection conn)  {
        String sql = "SELECT SUM(amount) AS totalRevenue FROM Payment";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getDouble("totalRevenue");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Integer> getBookingCountsLast7Days(Connection conn) throws SQLException, ClassNotFoundException {
        String sql = "SELECT generated_dates.bookingDay, COALESCE(COUNT(b.booking_id), 0) AS bookingCount " +
                "FROM ( " +
                "    SELECT CURDATE() - INTERVAL n DAY AS bookingDay FROM ( " +
                "        SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL " +
                "        SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 " +
                "    ) numbers " +
                ") generated_dates " +
                "LEFT JOIN Booking b ON DATE(b.created_at) = generated_dates.bookingDay " +
                "GROUP BY generated_dates.bookingDay " +
                "ORDER BY generated_dates.bookingDay DESC;";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            Map<String, Integer> bookingsMap = new LinkedHashMap<>();
            while (rs.next()) {
                bookingsMap.put(rs.getString("bookingDay"), rs.getInt("bookingCount"));
            }
            return bookingsMap;
        }
    }

}