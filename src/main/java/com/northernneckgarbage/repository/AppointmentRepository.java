package com.northernneckgarbage.repository;

import com.northernneckgarbage.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAppointmentDateAndAppointmentTimeBetween(LocalDate appointmentDate, LocalTime startTime, LocalTime endTime);


    List<Appointment> findByCustomerId(Long customerId);
}
