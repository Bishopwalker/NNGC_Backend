package com.northernneckgarbage.entity.dto;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CustomerRouteInfoDTO {

    private long id;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Double latitude;
    private Double longitude;
    private String county;


}