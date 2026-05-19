package com.clinic.repository;

import com.clinic.enums.AppointmentStatus;
import com.clinic.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDateTime BETWEEN :start AND :end " +
           "AND a.status != 'CANCELLED'")
    List<Appointment> findDoctorAppointmentsByDateRange(
        @Param("doctorId") Long doctorId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND DATE(a.appointmentDateTime) = DATE(:dateTime) " +
           "AND a.status != 'CANCELLED'")
    Long countDoctorAppointmentsForDay(
        @Param("doctorId") Long doctorId,
        @Param("dateTime") LocalDateTime dateTime
    );
}
