package com.northernneckgarbage.nngc.controller;

import com.northernneckgarbage.nngc.entity.Appointment;
import com.northernneckgarbage.nngc.entity.Customer;
import com.northernneckgarbage.nngc.service.AppointmentService;
import com.northernneckgarbage.nngc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;


    private final CustomerService customerService; // Assuming you have a CustomerService

    @PostMapping
    public Appointment createAppointment(@RequestBody Map<String, Object> payload) {
        log.info(payload.toString());
        return appointmentService.createAppointment(payload);
    }

    // Additional CRUD endpoints
}
