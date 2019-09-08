package com.example.petsmate.table;

import java.sql.Timestamp;

public class IotTable {
    private String id;
    private Integer pet_code;
    private Timestamp generate_time;
    private String latitude, longitude;
    private Integer heart_rate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPet_code() {
        return pet_code;
    }

    public void setPet_code(Integer pet_code) {
        this.pet_code = pet_code;
    }

    public Timestamp getGenerate_time() {
        return generate_time;
    }

    public void setGenerate_time(Timestamp generate_time) {
        this.generate_time = generate_time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(Integer heart_rate) {
        this.heart_rate = heart_rate;
    }

    @Override
    public String toString() {
        return "IotTable{" +
                "id='" + id + '\'' +
                ", pet_code=" + pet_code +
                ", generate_time=" + generate_time +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", heart_rate=" + heart_rate +
                '}';
    }
}
