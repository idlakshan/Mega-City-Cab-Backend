package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.PaymentDTO;

public interface PaymentBO {
    void savePayment(PaymentDTO paymentDTO) throws Exception;
}