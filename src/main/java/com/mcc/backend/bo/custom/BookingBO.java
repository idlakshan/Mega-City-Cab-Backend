package com.mcc.backend.bo.custom;

import com.mcc.backend.dto.BookingDTO;

public interface BookingBO {
    int saveBooking(BookingDTO bookingDTO) throws Exception;
}