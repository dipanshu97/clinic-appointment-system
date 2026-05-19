package com.clinic.service;

import com.clinic.cache.RedisService;
import com.clinic.exception.ClinicException;
import com.clinic.model.Patient;
import com.clinic.repository.PatientRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final RedisService redisService;

    public PatientService(PatientRepository patientRepository, RedisService redisService) {
        this.patientRepository = patientRepository;
        this.redisService = redisService;
    }

    // Register new patient
    public Patient registerPatient(Patient patient) {
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw ClinicException.patientAlreadyExists(patient.getEmail());
        }
        Patient saved = patientRepository.save(patient);
        // Cache patient
        redisService.set(RedisService.PATIENT_CACHE_KEY + saved.getId(),
            saved, RedisService.CACHE_TTL);
        return saved;
    }

    // Get patient by ID
    public Patient getPatientById(Long id) {
        // Check cache first
        Object cached = redisService.get(RedisService.PATIENT_CACHE_KEY + id);
        if (cached != null) {
            return (Patient) cached;
        }
        // Fetch from DB
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> ClinicException.patientNotFound(id));
        // Store in cache
        redisService.set(RedisService.PATIENT_CACHE_KEY + id,
            patient, RedisService.CACHE_TTL);
        return patient;
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Update patient
    public Patient updatePatient(Long id, Patient updatedPatient) {
        Patient existing = getPatientById(id);
        existing.setFirstName(updatedPatient.getFirstName());
        existing.setLastName(updatedPatient.getLastName());
        existing.setPhone(updatedPatient.getPhone());
        existing.setAddress(updatedPatient.getAddress());
        existing.setBloodGroup(updatedPatient.getBloodGroup());
        Patient saved = patientRepository.save(existing);
        // Invalidate cache
        redisService.delete(RedisService.PATIENT_CACHE_KEY + id);
        return saved;
    }

    // Delete patient
    public void deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
        redisService.delete(RedisService.PATIENT_CACHE_KEY + id);
    }
}
