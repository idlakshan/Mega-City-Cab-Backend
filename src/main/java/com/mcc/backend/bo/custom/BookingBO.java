package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.BookingDTO;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface BookingBO {
    int saveBooking(BookingDTO bookingDTO) throws Exception;
    List<BookingDTO> getAllBookings() throws Exception;
    List<BookingDTO> getBookingsByUserId(int userId) throws Exception;
    int getTotalBookings() throws Exception;
    double getTotalRevenue() throws Exception;
    Map<String, Integer> getBookingCountsLast7Days() throws Exception;
    List<BookingDTO> getBookingsByStatus(String status) throws Exception;

    void updateBookingStatus(int bookingId, String status) throws Exception;
    void updateBookingCarStatus(int carId, String status) throws Exception;
    void updateBookingDriverStatus(int driverId, String status) throws Exception;
    BookingDTO getBookingById(int bookingId) throws Exception;

    int getTotalBookingsByUserId(int userId) throws Exception;
    double getTotalSpendingByUserId(int userId) throws Exception;
    String getActiveSinceByUserId(int userId) throws Exception;
    String getFavoriteLocationByUserId(int userId) throws Exception;
    List<BookingDTO> getBookingsDetailsByUserId(int userId) throws Exception;
}