package com.taganga.one.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zones")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ZoneType type;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String rule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ZoneGeometry geometry;

    @Column(name = "display_order", nullable = false, unique = true)
    private int displayOrder;

    @ElementCollection
    @CollectionTable(name = "zone_coordinates", joinColumns = @JoinColumn(name = "zone_id"))
    @OrderColumn(name = "sort_order")
    private List<GeoPoint> coordinates = new ArrayList<>();

    protected Zone() {
    }

    public Zone(String code, ZoneType type, String name, String description, String rule,
            ZoneGeometry geometry, int displayOrder, List<GeoPoint> coordinates) {
        this.code = code;
        this.type = type;
        this.name = name;
        this.description = description;
        this.rule = rule;
        this.geometry = geometry;
        this.displayOrder = displayOrder;
        this.coordinates = new ArrayList<>(coordinates);
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public ZoneType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRule() {
        return rule;
    }

    public ZoneGeometry getGeometry() {
        return geometry;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public List<GeoPoint> getCoordinates() {
        return coordinates;
    }
}
