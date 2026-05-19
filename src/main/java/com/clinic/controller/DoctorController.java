package com.clinic.controller;

import com.clinic.enums.Specialization;
import com.clinic.model.Doctor;
import com.clinic.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // Register doctor — admin only
    @PostMapping("/admin/doctors/register")
    public ResponseEntity<Doctor> registerDoctor(@Valid @RequestBody Doctor doctor) {
        return new ResponseEntity<>(doctorService.registerDoctor(doctor), HttpStatus.CREATED);
    }

    // Get doctor by ID — public
    @GetMapping("/public/doctors/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // Get all doctors — public
    @GetMapping("/public/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // Get available doctors — public
    @GetMapping("/public/doctors/available")
    public ResponseEntity<List<Doctor>> getAvailableDoctors() {
        return ResponseEntity.ok(doctorService.getAvailableDoctors());
    }

    // Get doctors by specialization — public
    @GetMapping("/public/doctors/specialization/{specialization}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(
            @PathVariable Specialization specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }

    // Update doctor — admin only
    @PutMapping("/admin/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id,
                                                @Valid @RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctor));
    }

    // Update availability — doctor/admin
    @PatchMapping("/doctor/doctors/{id}/availability")
    public ResponseEntity<Doctor> updateAvailability(@PathVariable Long id,
                                                      @RequestParam boolean available) {
        return ResponseEntity.ok(doctorService.updateAvailability(id, available));
    }
}
