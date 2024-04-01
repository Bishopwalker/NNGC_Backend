package com.northernneckgarbage.dbConfig;

import com.northernneckgarbage.entity.dto.CustomerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {
    private CustomerDTO customer;
    private LocalTime appointmentTime;
    private LocalDate appointmentDate;
    private String appointmentType;
    private String message;

}
