package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.PaymentDAO;
import com.mcc.backend.entity.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
}