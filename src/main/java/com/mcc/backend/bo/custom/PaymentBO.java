package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.PaymentDTO;

import java.util.Map;

public interface PaymentBO {
    void savePayment(PaymentDTO paymentDTO) throws Exception;
    Map<String, Double> getTotalPaymentsLast7Days() throws Exception;
}