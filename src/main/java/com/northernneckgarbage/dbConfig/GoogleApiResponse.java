package com.northernneckgarbage.dbConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleApiResponse {
    private Principal principal;
}
