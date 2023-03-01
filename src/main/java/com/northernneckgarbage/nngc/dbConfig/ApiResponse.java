package com.northernneckgarbage.nngc.dbConfig;

import com.northernneckgarbage.nngc.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String token;
    private Customer customer;
}
