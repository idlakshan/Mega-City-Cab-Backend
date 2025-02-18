package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.dao.custom.PaymentDAO;
import com.mcc.backend.dto.PaymentDTO;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public void save(PaymentDTO paymentDTO) throws Exception {
        String sql = "INSERT INTO payment (booking_id, amount, payment_method, payment_status, payment_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = StripeCheckoutServlet.dataSource.getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setInt(1, paymentDTO.getBookingId());
            pstm.setDouble(2, paymentDTO.getAmount());
            pstm.setString(3, paymentDTO.getPaymentMethod());
            pstm.setString(4, paymentDTO.getPaymentStatus());
            pstm.setTimestamp(5, paymentDTO.getPaymentDate());

            pstm.executeUpdate();
        }
    }
}