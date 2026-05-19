package com.clinic.service;

import com.clinic.enums.AppointmentStatus;
import com.clinic.exception.ClinicException;
import com.clinic.model.Appointment;
import com.clinic.model.Doctor;
import com.clinic.model.Patient;
import com.clinic.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientService patientService,
                              DoctorService doctorService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    // Book appointment — uses Chain of Responsibility for validation
    public Appointment bookAppointment(Long patientId, Long doctorId,
                                       LocalDateTime appointmentDateTime, String reason) {
        // Chain of Responsibility — Validation Pipeline
        validateFutureTime(appointmentDateTime);
        validateDoctorAvailability(doctorId, appointmentDateTime);

        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctorById(doctorId);

        // Builder Pattern — Build Appointment object
        Appointment appointment = Appointment.builder()
            .patient(patient)
            .doctor(doctor)
            .appointmentDateTime(appointmentDateTime)
            .status(AppointmentStatus.SCHEDULED)
            .reason(reason)
            .tokenNumber(generateTokenNumber(doctorId, appointmentDateTime))
            .build();

        return appointmentRepository.save(appointment);
    }

    // Confirm appointment
    public Appointment confirmAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    // Cancel appointment
    public Appointment cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = getAppointmentById(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw ClinicException.appointmentAlreadyCancelled();
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setNotes("Cancelled: " + reason);
        return appointmentRepository.save(appointment);
    }

    // Reschedule appointment
    public Appointment rescheduleAppointment(Long appointmentId,
                                              LocalDateTime newDateTime) {
        Appointment appointment = getAppointmentById(appointmentId);
        validateFutureTime(newDateTime);
        validateDoctorAvailability(appointment.getDoctor().getId(), newDateTime);
        appointment.setAppointmentDateTime(newDateTime);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setTokenNumber(
            generateTokenNumber(appointment.getDoctor().getId(), newDateTime));
        return appointmentRepository.save(appointment);
    }

    // Complete appointment
    public Appointment completeAppointment(Long appointmentId, String notes) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setNotes(notes);
        return appointmentRepository.save(appointment);
    }

    // Get appointment by ID
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> ClinicException.appointmentNotFound(id));
    }

    // Get all appointments by patient
    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    // Get all appointments by doctor
    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    // Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // ── VALIDATION CHAIN (Chain of Responsibility Pattern) ──────

    private void validateFutureTime(LocalDateTime dateTime) {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw ClinicException.invalidAppointmentTime();
        }
    }

    private void validateDoctorAvailability(Long doctorId,
                                             LocalDateTime appointmentDateTime) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        if (!doctor.getAvailable()) {
            throw ClinicException.doctorNotAvailable();
        }
        // Check if doctor already has appointment at same time
        LocalDateTime startWindow = appointmentDateTime.minusMinutes(30);
        LocalDateTime endWindow = appointmentDateTime.plusMinutes(30);
        List<Appointment> conflicting =
            appointmentRepository.findDoctorAppointmentsByDateRange(
                doctorId, startWindow, endWindow);
        if (!conflicting.isEmpty()) {
            throw ClinicException.doctorNotAvailable();
        }
    }

    // ── TOKEN NUMBER GENERATOR (Factory Pattern) ─────────────────

    private Integer generateTokenNumber(Long doctorId,
                                         LocalDateTime appointmentDateTime) {
        Long count = appointmentRepository
            .countDoctorAppointmentsForDay(doctorId, appointmentDateTime);
        return count.intValue() + 1;
    }
}
