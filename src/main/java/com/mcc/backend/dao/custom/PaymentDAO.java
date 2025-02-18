package com.mcc.backend.dao.custom;

import com.mcc.backend.entity.Payment;

import java.sql.Connection;

public interface PaymentDAO {
    void save(Connection connection, Payment payment) throws Exception;
}