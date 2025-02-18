package com.mcc.backend.bo.custom.impl;

import com.mcc.backend.bo.custom.BookingBO;
import com.mcc.backend.dao.custom.BookingDAO;
import com.mcc.backend.dao.custom.impl.BookingDAOImpl;
import com.mcc.backend.dto.BookingDTO;

public class BookingBOImpl implements BookingBO {

    private BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    public int saveBooking(BookingDTO bookingDTO) throws Exception {
        return bookingDAO.save(bookingDTO);
    }
}