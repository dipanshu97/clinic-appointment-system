package com.clinic.service;

import com.clinic.cache.RedisService;
import com.clinic.enums.Specialization;
import com.clinic.exception.ClinicException;
import com.clinic.model.Doctor;
import com.clinic.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final RedisService redisService;

    public DoctorService(DoctorRepository doctorRepository, RedisService redisService) {
        this.doctorRepository = doctorRepository;
        this.redisService = redisService;
    }

    // Register doctor
    public Doctor registerDoctor(Doctor doctor) {
        Doctor saved = doctorRepository.save(doctor);
        redisService.set(RedisService.DOCTOR_CACHE_KEY + saved.getId(),
            saved, RedisService.CACHE_TTL);
        // Invalidate available doctors cache
        redisService.delete(RedisService.AVAILABLE_DOCTORS_KEY);
        return saved;
    }

    // Get doctor by ID
    public Doctor getDoctorById(Long id) {
        Object cached = redisService.get(RedisService.DOCTOR_CACHE_KEY + id);
        if (cached != null) {
            return (Doctor) cached;
        }
        Doctor doctor = doctorRepository.findById(id)
            .orElseThrow(() -> ClinicException.doctorNotFound(id));
        redisService.set(RedisService.DOCTOR_CACHE_KEY + id,
            doctor, RedisService.CACHE_TTL);
        return doctor;
    }

    // Get all doctors
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // Get available doctors
    @SuppressWarnings("unchecked")
    public List<Doctor> getAvailableDoctors() {
        Object cached = redisService.get(RedisService.AVAILABLE_DOCTORS_KEY);
        if (cached != null) {
            return (List<Doctor>) cached;
        }
        List<Doctor> doctors = doctorRepository.findByAvailableTrue();
        redisService.set(RedisService.AVAILABLE_DOCTORS_KEY,
            doctors, RedisService.CACHE_TTL);
        return doctors;
    }

    // Get doctors by specialization
    public List<Doctor> getDoctorsBySpecialization(Specialization specialization) {
        return doctorRepository.findBySpecializationAndAvailableTrue(specialization);
    }

    // Update doctor availability
    public Doctor updateAvailability(Long id, boolean available) {
        Doctor doctor = getDoctorById(id);
        doctor.setAvailable(available);
        Doctor saved = doctorRepository.save(doctor);
        // Invalidate caches
        redisService.delete(RedisService.DOCTOR_CACHE_KEY + id);
        redisService.delete(RedisService.AVAILABLE_DOCTORS_KEY);
        return saved;
    }

    // Update doctor
    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        Doctor existing = getDoctorById(id);
        existing.setFirstName(updatedDoctor.getFirstName());
        existing.setLastName(updatedDoctor.getLastName());
        existing.setPhone(updatedDoctor.getPhone());
        existing.setSpecialization(updatedDoctor.getSpecialization());
        existing.setExperienceYears(updatedDoctor.getExperienceYears());
        existing.setConsultationFee(updatedDoctor.getConsultationFee());
        Doctor saved = doctorRepository.save(existing);
        redisService.delete(RedisService.DOCTOR_CACHE_KEY + id);
        redisService.delete(RedisService.AVAILABLE_DOCTORS_KEY);
        return saved;
    }
}
