package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.dao.custom.impl.BookingDAOImpl;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.entity.Booking;
import com.mcc.backend.servlet.StripeCheckoutServlet;

import java.sql.Connection;
import java.sql.SQLException;

public class BookingBOImpl implements BookingBO {

    private BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    public int saveBooking(BookingDTO bookingDTO) throws Exception {
        try (Connection connection = StripeCheckoutServlet.dataSource.getConnection()) {
            Booking booking = new Booking();

            // Map DTO to Entity
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
}