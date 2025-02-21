package com.mcc.backend.dao.custom;

import com.mcc.backend.entity.Payment;

import java.sql.Connection;
import java.util.Map;

public interface PaymentDAO {
    void save(Connection connection, Payment payment) throws Exception;
    Map<String, Double> getTotalPaymentsLast7Days(Connection conn) throws Exception;
    Map<String, Double> getPaymentHistoryByUserId(Connection conn,int userId) throws Exception;
}