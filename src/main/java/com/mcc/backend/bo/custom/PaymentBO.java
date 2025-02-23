package com.mcc.backend.bo.custom;

import com.mcc.backend.bo.SuperBO;
import com.mcc.backend.dto.PaymentDTO;

import java.util.Map;

public interface PaymentBO extends SuperBO {
    void savePayment(PaymentDTO paymentDTO) throws Exception;
    Map<String, Double> getTotalPaymentsLast7Days() throws Exception;
    Map<String, Double> getPaymentHistoryByUserId(int userId) throws Exception;
}