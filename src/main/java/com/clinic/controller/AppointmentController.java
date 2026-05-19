package com.clinic.controller;

import com.clinic.model.Appointment;
import com.clinic.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Book appointment
    @PostMapping("/book")
    public ResponseEntity<Appointment> bookAppointment(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime appointmentDateTime,
            @RequestParam(required = false) String reason) {
        return new ResponseEntity<>(
            appointmentService.bookAppointment(patientId, doctorId, appointmentDateTime, reason),
            HttpStatus.CREATED);
    }

    // Confirm appointment
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Appointment> confirmAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    // Cancel appointment
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "No reason provided") String reason) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, reason));
    }

    // Reschedule appointment
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<Appointment> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                LocalDateTime newDateTime) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, newDateTime));
    }

    // Complete appointment
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Appointment> completeAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id, notes));
    }

    // Get appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // Get patient appointments
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getPatientAppointments(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    // Get doctor appointments
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorId));
    }

    // Get all appointments — admin only
    @GetMapping("/admin/all")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }
}
