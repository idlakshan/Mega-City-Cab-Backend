package com.mcc.backend.dao.custom;

import com.mcc.backend.entity.Booking;

import java.sql.Connection;

public interface BookingDAO {
    int save(Connection connection, Booking booking) throws Exception;
}