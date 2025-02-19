package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.dao.custom.impl.BookingDAOImpl;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.entity.Booking;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingBOImpl implements BookingBO {

    private BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    public int saveBooking(BookingDTO bookingDTO) throws Exception {
        try (Connection connection = StripeCheckoutServlet.dataSource.getConnection()) {
            Booking booking = new Booking();

            booking.setUserId(bookingDTO.getUserId());
            booking.setCarId(bookingDTO.getCarId());
            booking.setDriverId(bookingDTO.getDriverId());
            booking.setPickupLocation(bookingDTO.getPickupLocation());
            booking.setDropLocation(bookingDTO.getDropLocation());
            booking.setBookingDateTime(bookingDTO.getBookingDateTime());
            booking.setCustomerName(bookingDTO.getCustomerName());
            booking.setCustomerEmail(bookingDTO.getCustomerEmail());
            booking.setCustomerPhone(bookingDTO.getCustomerPhone());
            booking.setStatus(bookingDTO.getStatus());

            return bookingDAO.save(connection, booking);
        }
    }

    @Override
    public List<BookingDTO> getAllBookings() throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getAllBookings(conn);
        }
    }

    @Override
    public List<BookingDTO> getBookingsByUserId(int userId) throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getBookingsByUserId(conn, userId);
        }
    }

}