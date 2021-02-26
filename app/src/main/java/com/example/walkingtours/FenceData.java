package com.example.walkingtours;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class FenceData implements Serializable {

    private final String id;
    private final double lat;
    private final double lon;
    private final String address;
    private final float radius;
    private final String fenceColor;
    private final String description;
    private final String image;

    FenceData(String id, double lat, double lon, String address, float radius, String fenceColor, String description, String image) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.radius = radius;
        this.fenceColor = fenceColor;
        this.description = description;
        this.image = image;
    }

    String getId() {
        return id;
    }

    String getAddress() {
        return address;
    }

    float getRadius() {
        return radius;
    }

    String getFenceColor() {
        return fenceColor;
    }

    double getLat() {
        return lat;
    }

    double getLon() {
        return lon;
    }


    @NonNull
    @Override
    public String toString() {
        return "FenceData{" +
                "id='" + id + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", address='" + address + '\'' +
                ", radius=" + radius +
                ", fenceColor='" + fenceColor + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }
}
