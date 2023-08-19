package com.littlepay.triptracker;

public enum BusCharges {
    STOP1_STOP2("STOP1", "STOP2", "3.25"),
    STOP2_STOP3("STOP2", "STOP3", "5.50"),
    STOP1_STOP3("STOP1", "STOP3", "7.30");

    private final String stop1;
    private final String stop2;
    private final String fee;

    BusCharges(String stop1, String stop2, String fee) {
        this.stop1 = stop1;
        this.stop2 = stop2;
        this.fee = fee;
    }

    public String getStop1() {
        return stop1;
    }

    public String getStop2() {
        return stop2;
    }

    public String getFee() {
        return fee;
    }
}
