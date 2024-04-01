package com.northernneckgarbage.controller;

import com.northernneckgarbage.dbConfig.AppointmentResponse;
import com.northernneckgarbage.entity.Appointment;
import com.northernneckgarbage.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;




    @PostMapping("/create-appointment")
    public Appointment createAppointment(@RequestBody Map<String, Object> payload) {
        log.info(payload.toString());
        return appointmentService.createAppointment(payload);
    }

    @GetMapping("/reserved-appointments/{date}/{startTime}/{endTime}")
    public List<AppointmentResponse> getReservedAppointmentsByDateAndTime(@PathVariable String date, @PathVariable String startTime, @PathVariable String endTime) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        return appointmentService.findReservedAppointmentsByDateAndTime(localDate, start, end);
    }
    @GetMapping("/all")
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentService.findAllAppointments();
    }
    @DeleteMapping("/delete-appointment/{id}")
    public void deleteAppointmentById(@PathVariable Long id) {
        appointmentService.deleteAppointmentById(id);
    }

    @DeleteMapping("/delete-appointments-by-customer/{customerId}")
    public void deleteAppointmentsByCustomerId(@PathVariable Long customerId) {
        appointmentService.deleteAppointmentsByCustomerId(customerId);
    }

    @GetMapping("/all-scheduled-appointments")
    public List<Appointment> getAllScheduledAppointments() {
        return appointmentService.getAllScheduledAppointments();
    }
    // Additional CRUD endpoints
}
