package com.example.petsmate.table;

import java.sql.Timestamp;

public class CallTable {
    private Place startPlace, destinationPlace;
    private int serialNumber, code;
    private String guestId, driverId, ps;
    private boolean isCall, isShuttle;
    private Timestamp startTime, destinationTime, generateTime;

    public CallTable() {
    }

    public CallTable(Place startPlace, Place destinationPlace, int serialNumber, int code, String guestId, String driverId, String ps, boolean isCall, boolean isShuttle) {
        this.startPlace = startPlace;
        this.destinationPlace = destinationPlace;
        this.serialNumber = serialNumber;
        this.code = code;
        this.guestId = guestId;
        this.driverId = driverId;
        this.ps = ps;
        this.isCall = isCall;
        this.isShuttle = isShuttle;
    }

    public CallTable(Place startPlace, Place destinationPlace, int serialNumber, int code, String guestId, String driverId, boolean isCall) {
        this.startPlace = startPlace;
        this.destinationPlace = destinationPlace;
        this.serialNumber = serialNumber;
        this.code = code;
        this.guestId = guestId;
        this.driverId = driverId;
        this.isCall = isCall;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getDestinationTime() {
        return destinationTime;
    }

    public void setDestinationTime(Timestamp destinationTime) {
        this.destinationTime = destinationTime;
    }

    public Timestamp getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(Timestamp generateTime) {
        this.generateTime = generateTime;
    }

    public Place getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(Place startPlace) {
        this.startPlace = startPlace;
    }

    public Place getDestinationPlace() {
        return destinationPlace;
    }

    public void setDestinationPlace(Place destinationPlace) {
        this.destinationPlace = destinationPlace;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }

    public boolean isShuttle() {
        return isShuttle;
    }

    public void setShuttle(boolean shuttle) {
        isShuttle = shuttle;
    }

    @Override
    public String toString() {
        return "CallTable{" +
                "startPlace=" + startPlace +
                ", destinationPlace=" + destinationPlace +
                ", serialNumber=" + serialNumber +
                ", code=" + code +
                ", guestId='" + guestId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", ps='" + ps + '\'' +
                ", isCall=" + isCall +
                ", isShuttle=" + isShuttle +
                ", startTime=" + startTime +
                ", destinationTime=" + destinationTime +
                ", generateTime=" + generateTime +
                '}';
    }
}
