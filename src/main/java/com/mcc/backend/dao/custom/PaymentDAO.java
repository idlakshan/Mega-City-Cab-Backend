package com.mcc.backend.dao.custom;

import com.mcc.backend.dao.CrudDAO;
import com.mcc.backend.entity.Payment;

import java.sql.Connection;
import java.util.Map;

public interface PaymentDAO extends CrudDAO<Payment, Integer> {
    Map<String, Double> getTotalPaymentsLast7Days(Connection conn) throws Exception;
    Map<String, Double> getPaymentHistoryByUserId(Connection conn, int userId) throws Exception;
}