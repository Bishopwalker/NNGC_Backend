package com.northernneckgarbage.nngc.service;

import com.northernneckgarbage.nngc.dbConfig.ApiResponse;
import com.northernneckgarbage.nngc.dbConfig.AppointmentResponse;
import com.northernneckgarbage.nngc.entity.Appointment;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import com.northernneckgarbage.nngc.repository.AppointmentRepository;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final CustomerRepository customerRepository;

    private final AppointmentRepository appointmentRepository;
    private final CustomerService customerService;

    public Appointment createAppointment(Map<String, Object> payload) throws RuntimeException {
        if (payload.get("id") == null || payload.get("customer") == null || payload.get("appointmentDate") == null || payload.get("appointmentTime") == null) {
            throw new RuntimeException("Missing required fields");
        }

        Long customerId = Long.parseLong(payload.get("customer").toString());
        LocalDate appointmentDate = LocalDate.parse(payload.get("appointmentDate").toString(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalTime appointmentTime = LocalTime.parse(payload.get("appointmentTime").toString());

       var customer = customerRepository.findById(customerId).orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
log.info(customer.toString());
        Appointment appointment = Appointment.builder()
                .customer(customer)
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .build();

        return appointmentRepository.save(appointment);
    }

    // Additional CRUD methods
}

