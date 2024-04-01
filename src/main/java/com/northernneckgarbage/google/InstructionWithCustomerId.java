package com.northernneckgarbage.google;

import com.northernneckgarbage.entity.dto.CustomerRouteInfoDTO;
import lombok.Data;

@Data
public class InstructionWithCustomerId {
    private String instruction;
    private CustomerRouteInfoDTO customerInfo;

}