package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.PaymentBO;
import com.mcc.backend.dao.custom.PaymentDAO;
import com.mcc.backend.dao.custom.impl.PaymentDAOImpl;
import com.mcc.backend.dto.PaymentDTO;

public class PaymentBOImpl implements PaymentBO {

    private PaymentDAO paymentDAO = new PaymentDAOImpl();

    @Override
    public void savePayment(PaymentDTO paymentDTO) throws Exception {
        paymentDAO.save(paymentDTO);
    }
}