package com.mcc.backend.bo;

import com.mcc.backend.bo.custom.impl.*;


public class BOFactory {

    private static BOFactory boFactory;

    private BOFactory(){

    }

    public static BOFactory getBoFactory(){
        if (boFactory==null){
            boFactory=new BOFactory();
        }
        return boFactory;
    }

    public enum BOTypes{
        AUTH,BOOKING,CAR,CATEGORY,DRIVER,PAYMENT
    }

    public SuperBO getBO(BOTypes boTypes) {
        switch (boTypes) {
            case AUTH:
                return new AuthBOImpl();
            case BOOKING:
                return new BookingBOImpl();
            case CAR:
                return new CarBOImpl();
            case CATEGORY:
                return new CategoryBOImpl();
            case DRIVER:
                return new DriverBOImpl();
            case PAYMENT:
                return new PaymentBOImpl();
            default:
                return null;
        }
    }

}
