package com.mcc.backend.dao.custom;

import com.mcc.backend.dto.BookingDTO;

public interface BookingDAO {
    int save(BookingDTO bookingDTO) throws Exception;
}