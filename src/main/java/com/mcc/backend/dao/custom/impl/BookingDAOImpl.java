package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.entity.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
}