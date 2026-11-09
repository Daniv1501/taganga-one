package com.taganga.one.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class GeoPoint {

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    protected GeoPoint() {
    }

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
