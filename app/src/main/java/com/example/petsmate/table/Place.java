package com.example.petsmate.table;

public class Place {
    private String name; // 장소 명
    private String roadAddress; // 도로명 주소
    private String jibunAdress; // 지번 주소
    private String phoneNumber; // 전화번호
    private String x, lon; // 경도 LONG
    private String y, lat; // 위도 LAT
    private String distance; // 검색 좌표로부터 거리 (미터)

    public Place() {
    }

    public Place(String name, String roadAddress, String jibunAdress, String phoneNumber, String x, String y, String distance) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.jibunAdress = jibunAdress;
        this.phoneNumber = phoneNumber;
        this.x = x; lon = x;
        this.y = y; lat = y;
        this.distance = distance;
    }

    public Place(String name, String roadAddress, String jibunAdress, String phoneNumber, String x, String lon, String y, String lat, String distance) {
        this.name = name;
        this.roadAddress = roadAddress;
        this.jibunAdress = jibunAdress;
        this.phoneNumber = phoneNumber;
        this.x = x;
        this.lon = lon;
        this.y = y;
        this.lat = lat;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getJibunAdress() {
        return jibunAdress;
    }

    public void setJibunAdress(String jibunAdress) {
        this.jibunAdress = jibunAdress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}