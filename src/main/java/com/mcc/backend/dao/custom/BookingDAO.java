package com.mcc.backend.dao.custom;

import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.entity.Booking;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BookingDAO {
    int save(Connection connection, Booking booking) throws Exception;
    List<BookingDTO> getAllBookings(Connection connection) throws Exception;
    List<BookingDTO> getBookingsByUserId(Connection connection, int userId) throws Exception;
    int getTotalBookings(Connection conn) throws SQLException, ClassNotFoundException;
    double getTotalRevenue(Connection conn) throws SQLException, ClassNotFoundException;
    Map<String, Integer> getBookingCountsLast7Days(Connection conn) throws SQLException, ClassNotFoundException;
}