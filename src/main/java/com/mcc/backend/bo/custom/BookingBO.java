package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.BookingDTO;

import java.sql.Connection;
import java.util.List;

public interface BookingBO {
    int saveBooking(BookingDTO bookingDTO) throws Exception;
    List<BookingDTO> getAllBookings() throws Exception;
    List<BookingDTO> getBookingsByUserId(int userId) throws Exception;
    int getTotalBookings() throws Exception;
    double getTotalRevenue() throws Exception;
}