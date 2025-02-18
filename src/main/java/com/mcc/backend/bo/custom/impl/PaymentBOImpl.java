package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.PaymentBO;
import com.mcc.backend.dao.custom.PaymentDAO;
import com.mcc.backend.dao.custom.impl.PaymentDAOImpl;
import com.mcc.backend.dto.PaymentDTO;
import com.mcc.backend.entity.Payment;
import com.mcc.backend.servlet.StripeCheckoutServlet;

import java.sql.Connection;
import java.sql.SQLException;

public class PaymentBOImpl implements PaymentBO {

    private PaymentDAO paymentDAO = new PaymentDAOImpl();

    @Override
    public void savePayment(PaymentDTO paymentDTO) throws Exception {
        try (Connection connection = StripeCheckoutServlet.dataSource.getConnection()) {
            Payment payment = new Payment();

            // Map DTO to Entity
            payment.setBookingId(paymentDTO.getBookingId());
            payment.setAmount(paymentDTO.getAmount());
            payment.setPaymentMethod(paymentDTO.getPaymentMethod());
            payment.setPaymentStatus(paymentDTO.getPaymentStatus());
            payment.setPaymentDate(paymentDTO.getPaymentDate());

            paymentDAO.save(connection, payment);
        }
    }
}