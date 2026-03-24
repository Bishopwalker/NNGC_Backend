package com.northernneckgarbage.nngc.google;

import lombok.Data;

@Data
public class Geometry {
    private Bounds bounds;
    private Location location;
    private String locationType;
    private Viewport viewport;

    public Location getLocation() {
        return location.getLat() == 0 && location.getLng() == 0 ? null : location;
    }
}