package com.northernneckgarbage.nngc.dbConfig;

import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private List<String> token;
    private CustomerDTO customerDTO;
    private String message;
    private List<Customer> customers;
    private String status;

    T response;


}
