package com.northernneckgarbage.nngc.google;

import com.northernneckgarbage.nngc.entity.dto.CustomerRouteInfoDTO;
import lombok.Data;

@Data
public class InstructionWithCustomerId {
    private String instruction;
    private CustomerRouteInfoDTO customerInfo;

}