package com.littlepay.triptracker;

public enum TripType {
    CANCELLED ("CANCELLED"),
    INCOMPLETE ("INCOMPLETE") ,
    COMPLETED ("COMPLETED");
    private final String tripType;

    TripType(String tripType){
        this.tripType = tripType;
    }

}
