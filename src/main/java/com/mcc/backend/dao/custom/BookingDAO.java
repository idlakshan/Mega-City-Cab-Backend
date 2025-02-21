package com.mcc.backend.dao.custom;

import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.entity.Booking;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BookingDAO {
    int save(Connection connection, Booking booking) throws Exception;
    List<Booking> getAllBookings(Connection connection) throws Exception;
    List<BookingDTO> getBookingsByUserId(Connection connection, int userId) throws Exception;
    int getTotalBookings(Connection conn) throws SQLException, ClassNotFoundException;
    double getTotalRevenue(Connection conn) throws SQLException, ClassNotFoundException;
    Map<String, Integer> getBookingCountsLast7Days(Connection conn) throws SQLException, ClassNotFoundException;
    List<Booking> getBookingsByStatus(Connection conn, String status) throws Exception;

    void updateBookingStatus(Connection connection, int bookingId, String status) throws Exception;
    void updateBookingCarStatus(Connection connection, int carId, String status) throws Exception;
    void updateBookingDriverStatus(Connection connection, int driverId, String status) throws Exception;
    BookingDTO getBookingById(Connection connection, int bookingId) throws Exception;

    int getTotalBookingsByUserId(Connection connection,int userId);
    double getTotalSpendingByUserId(Connection connection,int userId);
    String getActiveSinceByUserId(Connection connection,int userId);
    String getFavoriteLocationByUserId(Connection connection,int userId);
}