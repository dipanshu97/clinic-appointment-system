package com.clinic.exception;

import org.springframework.http.HttpStatus;

public class ClinicException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public ClinicException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() { return errorCode; }
    public HttpStatus getHttpStatus() { return httpStatus; }

    // Predefined exceptions
    public static ClinicException patientNotFound(Long id) {
        return new ClinicException("PATIENT_001",
            "Patient not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public static ClinicException doctorNotFound(Long id) {
        return new ClinicException("DOCTOR_001",
            "Doctor not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public static ClinicException appointmentNotFound(Long id) {
        return new ClinicException("APPOINTMENT_001",
            "Appointment not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public static ClinicException patientAlreadyExists(String email) {
        return new ClinicException("PATIENT_002",
            "Patient already exists with email: " + email, HttpStatus.CONFLICT);
    }

    public static ClinicException doctorNotAvailable() {
        return new ClinicException("DOCTOR_002",
            "Doctor is not available for the requested time slot", HttpStatus.BAD_REQUEST);
    }

    public static ClinicException invalidAppointmentTime() {
        return new ClinicException("APPOINTMENT_002",
            "Appointment time must be in the future", HttpStatus.BAD_REQUEST);
    }

    public static ClinicException appointmentAlreadyCancelled() {
        return new ClinicException("APPOINTMENT_003",
            "Appointment is already cancelled", HttpStatus.BAD_REQUEST);
    }
}
