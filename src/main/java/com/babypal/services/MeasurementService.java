package com.babypal.services;

import java.util.List;

import com.babypal.models.Measurement;

public interface MeasurementService {
    Measurement createMeasurement(Measurement measurement, String username);

    Measurement updateMeasurement(Long measurementId, Measurement measurementDetails, String username);

    void deleteMeasurement(Long measurementId, String username);

    Measurement getMeasurementById(Long measurement, String username);

    List<Measurement> getAllMeasurementsByUsername(String username);

    List<Measurement> getAllMeasurements();
    
}
