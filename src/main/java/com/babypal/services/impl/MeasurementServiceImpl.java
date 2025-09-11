package com.babypal.services.impl;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.babypal.exceptions.UnauthorizedAccessException;
import com.babypal.models.Baby;
import com.babypal.models.Measurement;
import com.babypal.models.User;
import com.babypal.repositories.MeasurementRepository;
import com.babypal.repositories.BabyRepository;
import com.babypal.services.LogService;
import com.babypal.services.MeasurementService;
import com.babypal.services.UserService;

@Service
public class MeasurementServiceImpl implements MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final BabyRepository babyRepository;
    private final LogService logService;
    private final UserService userService;

    public MeasurementServiceImpl(MeasurementRepository measurementRepository, BabyRepository babyRepository, LogService logService, UserService userService) {
        this.measurementRepository = measurementRepository;
        this.babyRepository = babyRepository;
        this.logService = logService;
        this.userService = userService;
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public Measurement createMeasurement(Measurement measurement, String username) {
        Baby baby = babyRepository.findById(measurement.getBaby().getId())
                .orElseThrow(() -> new RuntimeException("Baby not found"));

        if (!isAdmin() && !baby.getCaregivers().contains(username)) {
            throw new UnauthorizedAccessException("Only caregivers can create measurements");
        }

        measurement.setBaby(baby);  // Set the fully loaded baby object
        measurement.setAuthor(username);
        Measurement savedMeasurement = measurementRepository.save(measurement);
        
        // Log measurement creation
        User user = userService.findByUsername(username);
        logService.logEntityCreate(username, user.getUserId(), "MEASUREMENT", savedMeasurement.getId(), "CREATE_MEASUREMENT");
        
        return savedMeasurement;
    }

    @Override
    public Measurement updateMeasurement(Long measurementId, Measurement measurementDetails, String username) {
        Measurement existingMeasurement = measurementRepository.findById(measurementId)
                .orElseThrow(() -> new RuntimeException("Measurement not found with id: " + measurementId));

        if (!isAdmin() && !existingMeasurement.getAuthor().equals(username)) {
            throw new UnauthorizedAccessException("Only admins and the author can update this measurement");
        }

        existingMeasurement.setTime(measurementDetails.getTime());
        existingMeasurement.setWeight(measurementDetails.getWeight());
        existingMeasurement.setHeight(measurementDetails.getHeight());
        existingMeasurement.setHeadCircumference(measurementDetails.getHeadCircumference());
        
        Measurement updatedMeasurement = measurementRepository.save(existingMeasurement);
        
        // Log measurement update
        User user = userService.findByUsername(username);
        logService.logEntityUpdate(username, user.getUserId(), "MEASUREMENT", measurementId, "UPDATE_MEASUREMENT");
        
        return updatedMeasurement;
    }

    @Override
    public void deleteMeasurement(Long measurementId, String username) {
        Measurement existingMeasurement = measurementRepository.findById(measurementId)
                .orElseThrow(() -> new RuntimeException("Measurement not found with id: " + measurementId));

        if (!isAdmin() && !existingMeasurement.getAuthor().equals(username)) {
            throw new UnauthorizedAccessException("Only admins and the author can delete this measurement");
        }

        measurementRepository.delete(existingMeasurement);
        
        // Log measurement deletion
        User user = userService.findByUsername(username);
        logService.logEntityDelete(username, user.getUserId(), "MEASUREMENT", measurementId, "DELETE_MEASUREMENT");
    }

    @Override
    public Measurement getMeasurementById(Long measurement, String username) {
        Measurement existingMeasurement = measurementRepository.findById(measurement)
                .orElseThrow(() -> new RuntimeException("Measurement not found with id: " + measurement));

        // Log measurement read
        User user = userService.findByUsername(username);
        logService.logEntityRead(username, user.getUserId(), "MEASUREMENT", measurement, "GET_MEASUREMENT");

        return existingMeasurement;
    }

    @Override
    public List<Measurement> getAllMeasurementsByUsername(String username) {
        return measurementRepository.findByAuthor(username);
    }

    @Override
    public List<Measurement> getAllMeasurements() {
        return measurementRepository.findAll();
    }
}
