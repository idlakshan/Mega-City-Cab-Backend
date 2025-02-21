package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.PaymentBO;
import com.mcc.backend.dao.custom.PaymentDAO;
import com.mcc.backend.dao.custom.impl.PaymentDAOImpl;
import com.mcc.backend.dto.PaymentDTO;
import com.mcc.backend.entity.Payment;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.StripeCheckoutServlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

public class PaymentBOImpl implements PaymentBO {

    private PaymentDAO paymentDAO = new PaymentDAOImpl();

    @Override
    public void savePayment(PaymentDTO paymentDTO) throws Exception {
        try (Connection connection = BookingServlet.dataSource.getConnection()) {
            Payment payment = new Payment();

            payment.setBookingId(paymentDTO.getBookingId());
            payment.setAmount(paymentDTO.getAmount());
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
            payment.setPaymentStatus(paymentDTO.getPaymentStatus());
            payment.setPaymentDate(paymentDTO.getPaymentDate());

            paymentDAO.save(connection, payment);
        }
    }

    @Override
    public Map<String, Double> getTotalPaymentsLast7Days() throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return paymentDAO.getTotalPaymentsLast7Days(conn);
        }
    }

    @Override
    public Map<String, Double> getPaymentHistoryByUserId(int userId) throws Exception {
        try (Connection conn = BookingServlet.dataSource.getConnection()) {
            return paymentDAO.getPaymentHistoryByUserId(conn,userId);
        }
    }
}