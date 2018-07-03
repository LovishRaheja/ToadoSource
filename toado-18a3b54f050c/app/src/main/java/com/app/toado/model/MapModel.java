package com.app.toado.model;

/**
 * Created by Silent Knight on 03-04-2018.
 */

public class MapModel {
    private String latitude;
    private String longitude;

    public MapModel() {}

    public MapModel(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {this.latitude = latitude;}
    public String getLatitude() {return latitude;}

    public void setLongitude(String longitude) {this.longitude = longitude;}
    public String getLongitude() {return longitude;}
}