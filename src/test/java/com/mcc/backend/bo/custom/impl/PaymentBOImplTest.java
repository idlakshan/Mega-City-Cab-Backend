package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.dao.custom.PaymentDAO;
import com.mcc.backend.dto.PaymentDTO;
import com.mcc.backend.entity.Payment;
import com.mcc.backend.servlet.BookingServlet;
import com.mcc.backend.servlet.InvoiceServlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentBOImplTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private PaymentDAO paymentDAO;

    private PaymentBOImpl paymentBO;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        paymentBO = new PaymentBOImpl();
        BookingServlet.dataSource = dataSource;
        InvoiceServlet.dataSource = dataSource;
    }

    @Test
    public void testSavePayment() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setBookingId(1);
        paymentDTO.setAmount(100.0);
        paymentDTO.setPaymentMethod("Stripe");
        paymentDTO.setPaymentStatus("Success");
        paymentDTO.setPaymentDate(new Timestamp(System.currentTimeMillis()));

        paymentBO.savePayment(paymentDTO);

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetTotalPaymentsLast7Days() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("paymentDay")).thenReturn("2023-10-01", "2023-10-02");
        when(resultSet.getDouble("totalAmount")).thenReturn(500.0, 300.0);

        Map<String, Double> payments = paymentBO.getTotalPaymentsLast7Days();
        assertNotNull(payments);
        assertEquals(2, payments.size());
        assertEquals(500.0, payments.get("2023-10-01"));
        assertEquals(300.0, payments.get("2023-10-02"));
    }

    @Test
    public void testGetPaymentHistoryByUserId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("month")).thenReturn("Oct", "Nov");
        when(resultSet.getDouble("total_amount")).thenReturn(500.0, 300.0);

        Map<String, Double> paymentHistory = paymentBO.getPaymentHistoryByUserId(1);
        assertNotNull(paymentHistory);
        assertEquals(2, paymentHistory.size());
        assertEquals(500.0, paymentHistory.get("Oct"));
        assertEquals(300.0, paymentHistory.get("Nov"));
    }

    @Test
    public void testGetPaymentByBookingId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("payment_id")).thenReturn(1);
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getDouble("amount")).thenReturn(100.0);
        when(resultSet.getString("payment_method")).thenReturn("Stripe");
        when(resultSet.getString("payment_status")).thenReturn("Success");
        when(resultSet.getTimestamp("payment_date")).thenReturn(new Timestamp(System.currentTimeMillis()));

        PaymentDTO paymentDTO = paymentBO.getPaymentByBookingId(1);
        assertNotNull(paymentDTO);
        assertEquals(1, paymentDTO.getPaymentId());
        assertEquals(1, paymentDTO.getBookingId());
        assertEquals(100.0, paymentDTO.getAmount());
        assertEquals("Stripe", paymentDTO.getPaymentMethod());
        assertEquals("Success", paymentDTO.getPaymentStatus());
    }
}