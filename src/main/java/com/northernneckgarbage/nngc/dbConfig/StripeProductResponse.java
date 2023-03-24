package com.northernneckgarbage.nngc.dbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeProductResponse<T> {

    private String message;
    private String id;
    private String name;
    private String price;
    private String description;
    private T response;


}
