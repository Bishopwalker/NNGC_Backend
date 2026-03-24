package com.northernneckgarbage.nngc.dbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeProductResponse<T> {

    private String message;
    private String id;
    private String name;
    private double price;
    private String description;
    private List<String> imageUrl;
    private T response;


}
