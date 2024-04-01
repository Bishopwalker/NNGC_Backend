package com.northernneckgarbage.entity.dto;

import com.google.maps.model.LatLng;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRouteDetailsDTO {
    private CustomerRouteInfoDTO customerInfo;
    private LatLng location;
    private String totalUsers;
}
