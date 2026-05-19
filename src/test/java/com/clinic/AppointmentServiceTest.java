package com.clinic;

import com.clinic.enums.AppointmentStatus;
import com.clinic.exception.ClinicException;
import com.clinic.model.Appointment;
import com.clinic.model.Doctor;
import com.clinic.model.Patient;
import com.clinic.repository.AppointmentRepository;
import com.clinic.service.AppointmentService;
import com.clinic.service.DoctorService;
import com.clinic.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private LocalDateTime futureDateTime;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
            .id(1L).firstName("John").lastName("Doe")
            .email("john@test.com").phone("9876543210")
            .build();

        doctor = Doctor.builder()
            .id(1L).firstName("Dr. Smith").lastName("Jones")
            .available(true).build();

        futureDateTime = LocalDateTime.now().plusDays(1);
    }

    @Test
    void bookAppointment_Success() {
        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(doctorService.getDoctorById(1L)).thenReturn(doctor);
        when(appointmentRepository.findDoctorAppointmentsByDateRange(any(), any(), any()))
            .thenReturn(new ArrayList<>());
        when(appointmentRepository.countDoctorAppointmentsForDay(any(), any())).thenReturn(0L);
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.bookAppointment(
            1L, 1L, futureDateTime, "Checkup");

        assertNotNull(result);
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        assertEquals(1, result.getTokenNumber());
        verify(appointmentRepository, times(1)).save(any());
    }

    @Test
    void bookAppointment_PastTime_ThrowsException() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);

        assertThrows(ClinicException.class, () ->
            appointmentService.bookAppointment(1L, 1L, pastDateTime, "Checkup"));
    }

    @Test
    void cancelAppointment_Success() {
        Appointment appointment = Appointment.builder()
            .id(1L).status(AppointmentStatus.SCHEDULED).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.cancelAppointment(1L, "Not feeling well");

        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelAppointment_AlreadyCancelled_ThrowsException() {
        Appointment appointment = Appointment.builder()
            .id(1L).status(AppointmentStatus.CANCELLED).build();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThrows(ClinicException.class, () ->
            appointmentService.cancelAppointment(1L, "reason"));
    }
}
