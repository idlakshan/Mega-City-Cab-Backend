package com.mcc.backend.dao;

import com.mcc.backend.dao.custom.impl.*;

public class DAOFactory {

    private static DAOFactory daoFactory;

    private DAOFactory(){
    }

    public static DAOFactory getDaoFactory(){
        if (daoFactory==null){
            daoFactory=new DAOFactory();
        }
        return daoFactory;
    }

    public enum DAOTypes{
        AUTH,BOOKING,CAR,CATEGORY,DRIVER,PAYMENT
    }

    public SuperDAO getDAO(DAOTypes types){
        switch (types){
            case AUTH:
                return new AuthDAOImpl();
            case BOOKING:
                return new BookingDAOImpl();
            case CAR:
                return new CarDAOImpl();
            case CATEGORY:
                return new CategoryDAOImpl();
            case DRIVER:
                return new DriverDAOImpl();
            case PAYMENT:
                return new PaymentDAOImpl();
            default:
                return null;
        }
    }
}
