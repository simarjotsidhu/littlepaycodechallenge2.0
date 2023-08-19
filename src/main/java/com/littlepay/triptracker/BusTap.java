package com.littlepay.triptracker;

import java.io.Serial;
import java.io.Serializable;


public class BusTap implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String dateTimeUTC;
    private String tapType;
    private String stopId;
    private String companyId;
    private String busId;
    private String panNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTimeUTC() {
        return dateTimeUTC;
    }

    public void setDateTimeUTC(String dateTimeUTC) {
        this.dateTimeUTC = dateTimeUTC;
    }

    public String getTapType() {
        return tapType;
    }

    public void setTapType(String tapType) {
        this.tapType = tapType;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }
}
