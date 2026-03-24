package com.northernneckgarbage.nngc.google;

import com.google.maps.model.LatLng;
import lombok.Data;

@Data
public class CustomerRouteInfo {
    private String customerId;
    private String name;
    private String phoneNumber;
    private String address;
    private LatLng location;
}
