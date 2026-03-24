package com.northernneckgarbage.nngc.service;

import com.northernneckgarbage.nngc.dbConfig.AppointmentResponse;
import com.northernneckgarbage.nngc.entity.Appointment;
import com.northernneckgarbage.nngc.entity.dto.CustomerDTO;
import com.northernneckgarbage.nngc.exceptions.AppointmentNotFoundException;
import com.northernneckgarbage.nngc.repository.AppointmentRepository;
import com.northernneckgarbage.nngc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final CustomerRepository customerRepository;

    private final AppointmentRepository appointmentRepository;
    private final CustomerService customerService;

    public Appointment createAppointment(Map<String, Object> payload) throws RuntimeException {
        if (payload.get("customer") == null || payload.get("appointmentDate") == null || payload.get("appointmentTime") == null || payload.get("appointmentType") == null)  {
            throw new RuntimeException("Missing required fields");
        }

        Long customerId = Long.parseLong(payload.get("customer").toString());
        LocalDate appointmentDate = LocalDate.parse(payload.get("appointmentDate").toString(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalTime appointmentTime = LocalTime.parse(payload.get("appointmentTime").toString());
        String appointmentType = payload.get("appointmentType").toString();
       var customer = customerRepository.findById(customerId).orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
log.info(customer.toString());
        var scheduledAppointments = findReservedAppointmentsByDateAndTime(appointmentDate, appointmentTime, appointmentTime.plusHours(2));
        if(!scheduledAppointments.isEmpty()) {
            throw new RuntimeException("Appointment already scheduled");
        }
        Appointment   appointment = Appointment.builder()
                .customer(customer)
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .appointmentType(appointmentType)

                .build();

        return  appointmentRepository.save(appointment) ;
    }

    public List<AppointmentResponse> findReservedAppointmentsByDateAndTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateAndAppointmentTimeBetween(date, startTime, endTime);
        return appointments.stream().map(appointment -> {
            CustomerDTO customerDTO = appointment.getCustomer().toCustomerDTO();

            return AppointmentResponse.builder()
                    .customer(customerDTO)
                    .appointmentDate(appointment.getAppointmentDate())
                    .appointmentTime(appointment.getAppointmentTime())
                    .appointmentType(appointment.getAppointmentType())
                    .build();
        }).collect(Collectors.toList());
    }
    public List<AppointmentResponse> findAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream().map(appointment -> {
            CustomerDTO customerDTO = appointment.getCustomer().toCustomerDTO();
            return AppointmentResponse.builder()
                    .customer(customerDTO)
                    .appointmentDate(appointment.getAppointmentDate())
                    .appointmentTime(appointment.getAppointmentTime())
                    .appointmentType(appointment.getAppointmentType())
                    .build();
        }).collect(Collectors.toList());
    }

    public void deleteAppointmentById(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new AppointmentNotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }

    public void deleteAppointmentsByCustomerId(Long customerId) {
        List<Appointment> appointments = appointmentRepository.findByCustomerId(customerId);
        if (appointments.isEmpty()) {
            throw new AppointmentNotFoundException("No appointments found for the customer");
        }
        appointmentRepository.deleteAll(appointments);
    }
    public List<Appointment> getAllScheduledAppointments() {
        return appointmentRepository.findAll();
    }

}

