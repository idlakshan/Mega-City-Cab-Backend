package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.dto.BookingDTO;
import com.mcc.backend.dto.CarDTO;
import com.mcc.backend.dto.DriverDTO;
import com.mcc.backend.dto.PaymentDTO;
import com.mcc.backend.entity.Booking;
import com.mcc.backend.entity.Car;
import com.mcc.backend.entity.Driver;
import com.mcc.backend.entity.Payment;

import java.sql.*;
import java.util.*;

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

    @Override
    public List<Booking> getAllBookings(Connection connection) throws Exception {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.user_id, c.car_id, c.car_name, c.car_number, d.driver_id, d.driver_name, d.driver_nic, b.pickup_location, b.drop_location, b.booking_datetime, b.customer_name, b.customer_email, b.customer_phone, b.status FROM booking b JOIN car c ON b.car_id = c.car_id JOIN driver d ON b.driver_id = d.driver_id ORDER BY b.booking_id DESC ;";
        try (PreparedStatement pstm = connection.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setPickupLocation(rs.getString("pickup_location"));
                booking.setDropLocation(rs.getString("drop_location"));
                booking.setBookingDateTime(rs.getTimestamp("booking_datetime"));
                booking.setCustomerName(rs.getString("customer_name"));
                booking.setCustomerEmail(rs.getString("customer_email"));
                booking.setCustomerPhone(rs.getString("customer_phone"));
                booking.setStatus(rs.getString("status"));


                Car car = new Car();
                car.setCarId(rs.getInt("car_id"));
                car.setCarName(rs.getString("car_name"));
                car.setCarNumber(rs.getString("car_number"));
                booking.setCar(car);


                Driver driver = new Driver();
                driver.setDriverId(rs.getInt("driver_id"));
                driver.setDriverName(rs.getString("driver_name"));
                driver.setDriverNic(rs.getString("driver_nic"));
                booking.setDriver(driver);

                bookings.add(booking);
            }
        }
        return bookings;
    }


    @Override
    public List<Booking> getBookingsByUserId(Connection connection, int userId) throws Exception {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE user_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, userId);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setUserId(rs.getInt("user_id"));
                    booking.setCarId(rs.getInt("car_id"));
                    booking.setDriverId(rs.getInt("driver_id"));
                    booking.setPickupLocation(rs.getString("pickup_location"));
                    booking.setDropLocation(rs.getString("drop_location"));
                    booking.setBookingDateTime(rs.getTimestamp("booking_datetime"));
                    booking.setCustomerName(rs.getString("customer_name"));
                    booking.setCustomerEmail(rs.getString("customer_email"));
                    booking.setCustomerPhone(rs.getString("customer_phone"));
                    booking.setStatus(rs.getString("status"));
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    @Override
    public int getTotalBookings(Connection conn) {
        String sql = "SELECT COUNT(*) AS totalBookings FROM Booking";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalBookings");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getTotalRevenue(Connection conn)  {
        String sql = "SELECT SUM(amount) AS totalRevenue FROM Payment";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getDouble("totalRevenue");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Integer> getBookingCountsLast7Days(Connection conn) throws SQLException, ClassNotFoundException {
        String sql = "SELECT generated_dates.bookingDay, COALESCE(COUNT(b.booking_id), 0) AS bookingCount " +
                "FROM ( " +
                "    SELECT CURDATE() - INTERVAL n DAY AS bookingDay FROM ( " +
                "        SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL " +
                "        SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 " +
                "    ) numbers " +
                ") generated_dates " +
                "LEFT JOIN Booking b ON DATE(b.created_at) = generated_dates.bookingDay " +
                "GROUP BY generated_dates.bookingDay " +
                "ORDER BY generated_dates.bookingDay DESC;";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            Map<String, Integer> bookingsMap = new LinkedHashMap<>();
            while (rs.next()) {
                bookingsMap.put(rs.getString("bookingDay"), rs.getInt("bookingCount"));
            }
            return bookingsMap;
        }
    }

    @Override
    public List<Booking> getBookingsByStatus(Connection conn, String status) throws Exception {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.user_id, c.car_id, c.car_name, c.car_number, d.driver_id, d.driver_name, " +
                "d.driver_nic, b.pickup_location, b.drop_location, b.booking_datetime, b.customer_name, b.customer_email," +
                " b.customer_phone, b.status FROM booking b JOIN car c ON b.car_id = c.car_id JOIN driver d ON" +
                " b.driver_id = d.driver_id WHERE b.status = ?;";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, status);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setUserId(rs.getInt("user_id"));
                    booking.setPickupLocation(rs.getString("pickup_location"));
                    booking.setDropLocation(rs.getString("drop_location"));
                    booking.setBookingDateTime(rs.getTimestamp("booking_datetime"));
                    booking.setCustomerName(rs.getString("customer_name"));
                    booking.setCustomerEmail(rs.getString("customer_email"));
                    booking.setCustomerPhone(rs.getString("customer_phone"));
                    booking.setStatus(rs.getString("status"));


                    Car car = new Car();
                    car.setCarId(rs.getInt("car_id"));
                    car.setCarName(rs.getString("car_name"));
                    car.setCarNumber(rs.getString("car_number"));
                    booking.setCar(car);


                    Driver driver = new Driver();
                    driver.setDriverId(rs.getInt("driver_id"));
                    driver.setDriverName(rs.getString("driver_name"));
                    driver.setDriverNic(rs.getString("driver_nic"));
                    booking.setDriver(driver);

                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    @Override
    public void updateBookingStatus(Connection connection, int bookingId, String status) throws Exception {
        String sql = "UPDATE booking SET status = ? WHERE booking_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, status);
            pstm.setInt(2, bookingId);
            pstm.executeUpdate();
        }
    }

    @Override
    public void updateBookingCarStatus(Connection connection, int carId, String status) throws Exception {
        String sql = "UPDATE car SET status = ? WHERE car_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, status);
            pstm.setInt(2, carId);
            pstm.executeUpdate();
        }
    }

    @Override
    public void updateBookingDriverStatus(Connection connection, int driverId, String status) throws Exception {
        String sql = "UPDATE driver SET status = ? WHERE driver_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, status);
            pstm.setInt(2, driverId);
            pstm.executeUpdate();
        }
    }

    @Override
    public Booking getBookingById(Connection connection, int bookingId) throws Exception {
        String sql = "SELECT * FROM booking WHERE booking_id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, bookingId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setUserId(rs.getInt("user_id"));
                    booking.setCarId(rs.getInt("car_id"));
                    booking.setDriverId(rs.getInt("driver_id"));
                    booking.setPickupLocation(rs.getString("pickup_location"));
                    booking.setDropLocation(rs.getString("drop_location"));
                    booking.setBookingDateTime(rs.getTimestamp("booking_datetime"));
                    booking.setCustomerName(rs.getString("customer_name"));
                    booking.setCustomerEmail(rs.getString("customer_email"));
                    booking.setCustomerPhone(rs.getString("customer_phone"));
                    booking.setStatus(rs.getString("status"));
                    return booking;
                }
            }
        }
        return null;
    }

    @Override
    public int getTotalBookingsByUserId(Connection connection,int userId) {
        String sql="SELECT COUNT(*) AS total_bookings FROM Booking where user_id=?";
        try (PreparedStatement pstm =connection.prepareStatement(sql)) {
            pstm.setInt(1, userId);
            ResultSet rst = pstm.executeQuery();
            if (rst.next()) {
                return rst.getInt("total_bookings");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getTotalSpendingByUserId(Connection connection,int userId) {
        String sql="SELECT SUM(amount) AS total_spending FROM payment WHERE booking_id IN (SELECT booking_id FROM booking WHERE user_id = ?)";
        try (PreparedStatement pstm =connection.prepareStatement(sql)) {
            pstm.setInt(1, userId);
            ResultSet rst = pstm.executeQuery();
            if (rst.next()) {
                return rst.getDouble("total_spending");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public String getActiveSinceByUserId(Connection connection,int userId) {
        String sql="SELECT MIN(booking_datetime) AS active_since FROM booking WHERE user_id = ?";
        try (PreparedStatement pstm =connection.prepareStatement(sql)) {
            pstm.setInt(1, userId);
            ResultSet rst = pstm.executeQuery();
            if (rst.next()) {
                return rst.getString("active_since");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    @Override
    public String getFavoriteLocationByUserId(Connection connection,int userId) {
        String sql="SELECT pickup_location, COUNT(*) AS location_count FROM booking WHERE user_id = ? GROUP BY pickup_location ORDER BY location_count DESC LIMIT 1";
        try (PreparedStatement pstm =connection.prepareStatement(sql)) {
            pstm.setInt(1, userId);
            ResultSet rst = pstm.executeQuery();
            if (rst.next()) {
                return rst.getString("pickup_location");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    @Override
    public List<Booking> getBookingsWithDetailsByUserId(Connection connection, int userId) throws Exception {
        String sql = "SELECT b.*, c.*, d.*, p.* " +
                "FROM booking b " +
                "LEFT JOIN car c ON b.car_id = c.car_id " +
                "LEFT JOIN driver d ON b.driver_id = d.driver_id " +
                "LEFT JOIN payment p ON b.booking_id = p.booking_id " +
                "WHERE b.user_id = ? order by p.payment_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Booking> bookings = new ArrayList<>();
                while (rs.next()) {

                    Booking booking = new Booking();
                    booking.setBookingId(rs.getInt("booking_id"));
                    booking.setUserId(rs.getInt("user_id"));
                    booking.setCarId(rs.getInt("car_id"));
                    booking.setDriverId(rs.getInt("driver_id"));
                    booking.setPickupLocation(rs.getString("pickup_location"));
                    booking.setDropLocation(rs.getString("drop_location"));
                    booking.setBookingDateTime(rs.getTimestamp("booking_datetime"));
                    booking.setCustomerName(rs.getString("customer_name"));
                    booking.setCustomerEmail(rs.getString("customer_email"));
                    booking.setCustomerPhone(rs.getString("customer_phone"));
                    booking.setStatus(rs.getString("status"));


                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setAmount(rs.getDouble("amount"));
                    payment.setPaymentMethod(rs.getString("payment_method"));
                    payment.setPaymentStatus(rs.getString("payment_status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date"));
                    booking.setPayment(payment);

                    bookings.add(booking);
                }
                return bookings;
            }
        }
    }


}