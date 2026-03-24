package com.northernneckgarbage.nngc.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMessageDTO {
    private String userName;
    private String userPhone;
    private String userEmail;
    private String message;
}
