package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.PaymentDAO;
import com.mcc.backend.entity.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public void save(Connection connection, Payment payment) throws Exception {
        String sql = "INSERT INTO payment (booking_id, amount, payment_method, payment_status, payment_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, payment.getBookingId());
            pstm.setDouble(2, payment.getAmount());
            pstm.setString(3, payment.getPaymentMethod());
            pstm.setString(4, payment.getPaymentStatus());
            pstm.setTimestamp(5, payment.getPaymentDate());

            pstm.executeUpdate();
        }
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

}