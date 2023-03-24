package com.northernneckgarbage.nngc.entity.dto;

 ;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {



        //House number and street
    private String line1;
    //Apt, suite, etc.
    private String line2;
     private String city;
        private String state;
        private String zipCode;

}
