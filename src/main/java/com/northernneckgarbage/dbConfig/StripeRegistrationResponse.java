package com.northernneckgarbage.dbConfig;

import com.northernneckgarbage.entity.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeRegistrationResponse<T> {

        private String error;
        private T data;

      private String message;
    private  CustomerDTO customerDTO;

}
