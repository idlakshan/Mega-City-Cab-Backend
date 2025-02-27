package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.PaymentDAO;
import com.mcc.backend.entity.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public Payment findById(Connection connection, Integer id) throws SQLException {
      return null;
    }

    @Override
    public List<Payment> findAll(Connection connection) throws SQLException {
       return null;
    }

    @Override
    public boolean save(Connection connection, Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (booking_id, amount, payment_method, payment_status, payment_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, payment.getBookingId());
            pstm.setDouble(2, payment.getAmount());
            pstm.setString(3, payment.getPaymentMethod());
            pstm.setString(4, payment.getPaymentStatus());
            pstm.setTimestamp(5, payment.getPaymentDate());
            return pstm.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Connection connection, Payment payment) throws SQLException {
       return false;
    }

    @Override
    public boolean delete(Connection connection, Integer id) throws SQLException {
        return false;
    }

    @Override
    public Map<String, Double> getTotalPaymentsLast7Days(Connection conn) throws Exception {
        String sql = "SELECT generated_dates.paymentDay, COALESCE(SUM(p.amount), 0) AS totalAmount FROM (SELECT CURDATE() - INTERVAL n DAY AS paymentDay FROM ( SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6) AS numbers) AS generated_dates LEFT JOIN Payment p ON DATE(p.payment_date) = generated_dates.paymentDay GROUP BY generated_dates.paymentDay ORDER BY generated_dates.paymentDay DESC;";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            Map<String, Double> paymentsMap = new LinkedHashMap<>();
            while (rs.next()) {
                paymentsMap.put(rs.getString("paymentDay"), rs.getDouble("totalAmount"));
            }
            return paymentsMap;
        }
    }

    @Override
    public Map<String, Double> getPaymentHistoryByUserId(Connection conn, int userId) throws Exception {
        String sql = "SELECT DATE_FORMAT(payment_date, '%b') AS month, SUM(amount) AS total_amount " +
                "FROM payment " +
                "WHERE booking_id IN (SELECT booking_id FROM booking WHERE user_id = ?) " +
                "GROUP BY DATE_FORMAT(payment_date, '%b')";

        Map<String, Double> paymentHistory = new HashMap<>();
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, userId);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String month = rs.getString("month");
                    double totalAmount = rs.getDouble("total_amount");
                    paymentHistory.put(month, totalAmount);
                }
            }
        }
        return paymentHistory;
    }

    @Override
    public Payment getPaymentByBookingId(Connection conn, int bookingId) throws Exception {
        String sql = "SELECT payment_id, booking_id, amount, payment_method, payment_status, payment_date " +
                "FROM payment WHERE booking_id = ?";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, bookingId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setBookingId(rs.getInt("booking_id"));
                    payment.setAmount(rs.getDouble("amount"));
                    payment.setPaymentMethod(rs.getString("payment_method"));
                    payment.setPaymentStatus(rs.getString("payment_status"));
                    payment.setPaymentDate(rs.getTimestamp("payment_date"));
                    return payment;
                }
            }
        }
        return null;
    }

}