package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class BookingDAOImpl implements BookingDAO {

    @Override
    public int save(BookingDTO bookingDTO) throws Exception {
        String sql = "INSERT INTO booking (user_id, car_id, driver_id, pickup_location, drop_location, booking_datetime, customer_name, customer_email, customer_phone, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = StripeCheckoutServlet.dataSource.getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setInt(1, bookingDTO.getUserId());
            pstm.setInt(2, bookingDTO.getCarId());
            pstm.setInt(3, bookingDTO.getDriverId());
            pstm.setString(4, bookingDTO.getPickupLocation());
            pstm.setString(5, bookingDTO.getDropLocation());
            pstm.setTimestamp(6, bookingDTO.getBookingDateTime());
            pstm.setString(7, bookingDTO.getCustomerName());
            pstm.setString(8, bookingDTO.getCustomerEmail());
            pstm.setString(9, bookingDTO.getCustomerPhone());
            pstm.setString(10, bookingDTO.getStatus());

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