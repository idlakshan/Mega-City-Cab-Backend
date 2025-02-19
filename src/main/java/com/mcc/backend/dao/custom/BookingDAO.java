package com.mcc.backend.dao.custom;

import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.entity.Booking;

import java.sql.Connection;
import java.util.List;

public interface BookingDAO {
    int save(Connection connection, Booking booking) throws Exception;
    List<BookingDTO> getAllBookings(Connection connection) throws Exception;
    List<BookingDTO> getBookingsByUserId(Connection connection, int userId) throws Exception;
}