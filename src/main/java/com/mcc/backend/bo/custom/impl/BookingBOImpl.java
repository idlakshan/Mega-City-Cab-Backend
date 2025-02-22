package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.bo.custom.CarBO;
import com.mcc.backend.bo.custom.DriverBO;
import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.dao.custom.impl.BookingDAOImpl;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.dto.PaymentDTO;
import com.mcc.backend.entity.Booking;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            List<Booking> bookingsByStatus = bookingDAO.getAllBookings(conn);
            List<BookingDTO> bookingDTOs = new ArrayList<>();

            for (Booking booking : bookingsByStatus) {
                BookingDTO dto = new BookingDTO();
                dto.setBookingId(booking.getBookingId());
                dto.setUserId(booking.getUserId());
                dto.setPickupLocation(booking.getPickupLocation());
                dto.setDropLocation(booking.getDropLocation());
                dto.setBookingDateTime(booking.getBookingDateTime());
                dto.setCustomerName(booking.getCustomerName());
                dto.setCustomerEmail(booking.getCustomerEmail());
                dto.setCustomerPhone(booking.getCustomerPhone());
                dto.setStatus(booking.getStatus());


                if (booking.getCar() != null) {
                    CarDTO carDTO = new CarDTO();
                    carDTO.setCarId(booking.getCar().getCarId());
                    carDTO.setCarName(booking.getCar().getCarName());
                    carDTO.setCarNumber(booking.getCar().getCarNumber());
                    dto.setCar(carDTO);
                }

                if (booking.getDriver() != null) {
                    DriverDTO driverDTO = new DriverDTO();
                    driverDTO.setDriverId(booking.getDriver().getDriverId());
                    driverDTO.setDriverName(booking.getDriver().getDriverName());
                    driverDTO.setDriverNic(booking.getDriver().getDriverNic());
                    dto.setDriver(driverDTO);
                }

                bookingDTOs.add(dto);
            }

            return bookingDTOs;
        }
    }


    @Override
    public List<BookingDTO> getBookingsByUserId(int userId) throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            List<Booking> bookingsByUserId = bookingDAO.getBookingsByUserId(conn, userId);
            List<BookingDTO> bookingDTOs = new ArrayList<>();
            for (Booking booking : bookingsByUserId) {
                BookingDTO dto = new BookingDTO();
                dto.setBookingId(booking.getBookingId());
                dto.setUserId(booking.getUserId());
                dto.setCarId(booking.getCarId());
                dto.setDriverId(booking.getDriverId());
                dto.setPickupLocation(booking.getPickupLocation());
                dto.setDropLocation(booking.getDropLocation());
                dto.setBookingDateTime(booking.getBookingDateTime());
                dto.setCustomerName(booking.getCustomerName());
                dto.setCustomerEmail(booking.getCustomerEmail());
                dto.setCustomerPhone(booking.getCustomerPhone());
                dto.setStatus(booking.getStatus());

                bookingDTOs.add(dto);

            }
            return bookingDTOs;
        }
    }

    @Override
    public int getTotalBookings() throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getTotalBookings(conn);
        }
    }

    @Override
    public double getTotalRevenue() throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getTotalRevenue(conn);
        }
    }

    @Override
    public Map<String, Integer> getBookingCountsLast7Days() throws SQLException, ClassNotFoundException {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getBookingCountsLast7Days(conn);
        }
    }

    @Override
    public List<BookingDTO> getBookingsByStatus(String status) throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            List<Booking> bookingsByStatus = bookingDAO.getBookingsByStatus(conn, status);
            List<BookingDTO> bookingDTOs = new ArrayList<>();

            for (Booking booking : bookingsByStatus) {
                BookingDTO dto = new BookingDTO();
                dto.setBookingId(booking.getBookingId());
                dto.setUserId(booking.getUserId());
                dto.setPickupLocation(booking.getPickupLocation());
                dto.setDropLocation(booking.getDropLocation());
                dto.setBookingDateTime(booking.getBookingDateTime());
                dto.setCustomerName(booking.getCustomerName());
                dto.setCustomerEmail(booking.getCustomerEmail());
                dto.setCustomerPhone(booking.getCustomerPhone());
                dto.setStatus(booking.getStatus());


                if (booking.getCar() != null) {
                    CarDTO carDTO = new CarDTO();
                    carDTO.setCarId(booking.getCar().getCarId());
                    carDTO.setCarName(booking.getCar().getCarName());
                    carDTO.setCarNumber(booking.getCar().getCarNumber());
                    dto.setCar(carDTO);
                }

                if (booking.getDriver() != null) {
                    DriverDTO driverDTO = new DriverDTO();
                    driverDTO.setDriverId(booking.getDriver().getDriverId());
                    driverDTO.setDriverName(booking.getDriver().getDriverName());
                    driverDTO.setDriverNic(booking.getDriver().getDriverNic());
                    dto.setDriver(driverDTO);
                }

                bookingDTOs.add(dto);
            }

            return bookingDTOs;
        }
    }

    @Override
    public void updateBookingStatus(int bookingId, String status) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            bookingDAO.updateBookingStatus(connection, bookingId, status);
        }
    }

    @Override
    public void updateBookingCarStatus(int carId, String status) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            bookingDAO.updateBookingCarStatus(connection, carId, status);
        }
    }

    @Override
    public void updateBookingDriverStatus(int driverId, String status) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            bookingDAO.updateBookingDriverStatus(connection, driverId, status);
        }
    }

    @Override
    public BookingDTO getBookingById(int bookingId) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            Booking bookingById = bookingDAO.getBookingById(connection, bookingId);
            BookingDTO dto = new BookingDTO();
            dto.setBookingId(bookingById.getBookingId());
            dto.setUserId(bookingById.getUserId());
            dto.setCarId(bookingById.getCarId());
            dto.setDriverId(bookingById.getDriverId());
            dto.setPickupLocation(bookingById.getPickupLocation());
            dto.setDropLocation(bookingById.getDropLocation());
            dto.setBookingDateTime(bookingById.getBookingDateTime());
            dto.setCustomerName(bookingById.getCustomerName());
            dto.setCustomerEmail(bookingById.getCustomerEmail());
            dto.setCustomerPhone(bookingById.getCustomerPhone());
            dto.setStatus(bookingById.getStatus());

            return dto;
        }
    }

    @Override
    public int getTotalBookingsByUserId(int userId) throws Exception  {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getTotalBookingsByUserId(connection,userId);
        }

    }

    @Override
    public double getTotalSpendingByUserId(int userId) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getTotalSpendingByUserId(connection,userId);
        }

    }

    @Override
    public String getActiveSinceByUserId(int userId) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getActiveSinceByUserId(connection,userId);
        }

    }

    @Override
    public String getFavoriteLocationByUserId(int userId) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            return bookingDAO.getFavoriteLocationByUserId(connection,userId);
        }

    }

    @Override
    public List<BookingDTO> getBookingsDetailsByUserId(int userId) throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            List<Booking> bookingsWithDetailsByUserId = bookingDAO.getBookingsWithDetailsByUserId(conn, userId);

            List<BookingDTO> bookingDTOs = new ArrayList<>();
            for (Booking booking : bookingsWithDetailsByUserId) {
                BookingDTO bookingDTO = new BookingDTO();
                bookingDTO.setBookingId(booking.getBookingId());
                bookingDTO.setUserId(booking.getUserId());
                bookingDTO.setCarId(booking.getCarId());
                bookingDTO.setDriverId(booking.getDriverId());
                bookingDTO.setPickupLocation(booking.getPickupLocation());
                bookingDTO.setDropLocation(booking.getDropLocation());
                bookingDTO.setBookingDateTime(booking.getBookingDateTime());
                bookingDTO.setCustomerName(booking.getCustomerName());
                bookingDTO.setCustomerEmail(booking.getCustomerEmail());
                bookingDTO.setCustomerPhone(booking.getCustomerPhone());
                bookingDTO.setStatus(booking.getStatus());

                PaymentDTO paymentDTO = new PaymentDTO();
                paymentDTO.setPaymentId(booking.getPayment().getPaymentId());
                paymentDTO.setAmount(booking.getPayment().getAmount());
                paymentDTO.setPaymentMethod(booking.getPayment().getPaymentMethod());
                paymentDTO.setPaymentStatus(booking.getPayment().getPaymentStatus());
                paymentDTO.setPaymentDate(booking.getPayment().getPaymentDate());
                bookingDTO.setPayment(paymentDTO);

                bookingDTOs.add(bookingDTO);

            }

            return bookingDTOs;
        }
    }


}