package com.mcc.backend.dao.custom.impl;

import com.mcc.backend.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentDAOImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private PaymentDAOImpl paymentDAO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testSave() throws SQLException {
        Payment payment = new Payment();
        payment.setBookingId(1);
        payment.setAmount(100.0);
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentStatus("Paid");
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));

        when(preparedStatement.executeUpdate()).thenReturn(1);


        boolean result = paymentDAO.save(connection, payment);

        assertTrue(result);
        verify(preparedStatement, times(1)).setInt(1, 1);
        verify(preparedStatement, times(1)).setDouble(2, 100.0);
        verify(preparedStatement, times(1)).setString(3, "Credit Card");
        verify(preparedStatement, times(1)).setString(4, "Paid");
        verify(preparedStatement, times(1)).setTimestamp(5, payment.getPaymentDate());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetTotalPaymentsLast7Days() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("paymentDay")).thenReturn("2023-10-01");
        when(resultSet.getDouble("totalAmount")).thenReturn(500.0);

        Map<String, Double> paymentsMap = paymentDAO.getTotalPaymentsLast7Days(connection);

        assertNotNull(paymentsMap);
        assertEquals(1, paymentsMap.size());
        assertEquals(500.0, paymentsMap.get("2023-10-01"));
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetPaymentHistoryByUserId() throws Exception {
        int userId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("month")).thenReturn("Oct");
        when(resultSet.getDouble("total_amount")).thenReturn(1000.0);

        Map<String, Double> paymentHistory = paymentDAO.getPaymentHistoryByUserId(connection, userId);

        assertNotNull(paymentHistory);
        assertEquals(1, paymentHistory.size());
        assertEquals(1000.0, paymentHistory.get("Oct"));
        verify(preparedStatement, times(1)).setInt(1, userId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testGetPaymentByBookingId() throws Exception {
        int bookingId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("payment_id")).thenReturn(1);
        when(resultSet.getInt("booking_id")).thenReturn(1);
        when(resultSet.getDouble("amount")).thenReturn(100.0);
        when(resultSet.getString("payment_method")).thenReturn("Credit Card");
        when(resultSet.getString("payment_status")).thenReturn("Paid");
        when(resultSet.getTimestamp("payment_date")).thenReturn(new Timestamp(System.currentTimeMillis()));


        Payment payment = paymentDAO.getPaymentByBookingId(connection, bookingId);

        assertNotNull(payment);
        assertEquals(1, payment.getPaymentId());
        assertEquals(1, payment.getBookingId());
        assertEquals(100.0, payment.getAmount());
        assertEquals("Credit Card", payment.getPaymentMethod());
        assertEquals("Paid", payment.getPaymentStatus());
        verify(preparedStatement, times(1)).setInt(1, bookingId);
        verify(preparedStatement, times(1)).executeQuery();
    }

    @Test
    void testFindById() throws SQLException {
        int paymentId = 1;


        Payment payment = paymentDAO.findById(connection, paymentId);

        assertNull(payment);
        verify(preparedStatement, times(0)).setInt(1, paymentId);
    }

    @Test
    void testFindAll() throws SQLException {
        List<Payment> payments = paymentDAO.findAll(connection);

        assertNull(payments);
        verify(preparedStatement, times(0)).executeQuery();
    }

    @Test
    void testUpdate() throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(1);
        payment.setBookingId(1);
        payment.setAmount(100.0);
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentStatus("Paid");
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));

        boolean result = paymentDAO.update(connection, payment);

        assertFalse(result);
        verify(preparedStatement, times(0)).executeUpdate();
    }

    @Test
    void testDelete() throws SQLException {
        int paymentId = 1;

        boolean result = paymentDAO.delete(connection, paymentId);

        assertFalse(result);
        verify(preparedStatement, times(0)).executeUpdate();
    }
}