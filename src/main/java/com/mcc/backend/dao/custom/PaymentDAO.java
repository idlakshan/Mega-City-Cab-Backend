package com.mcc.backend.dao.custom;

import com.mcc.backend.dto.PaymentDTO;

public interface PaymentDAO {
    void save(PaymentDTO paymentDTO) throws Exception;
}