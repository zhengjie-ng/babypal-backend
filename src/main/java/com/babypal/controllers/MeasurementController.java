package com.babypal.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.babypal.models.Measurement;
import com.babypal.services.MeasurementService;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @PostMapping
    public Measurement createMeasurement(@RequestBody Measurement measurement, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return measurementService.createMeasurement(measurement, username);

    }

    @GetMapping
    public List<Measurement> getMeasurements(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return measurementService.getAllMeasurementsByUsername(username);
    }

    @GetMapping("/{measurementId}")
    public Measurement getMeasurementById(@PathVariable Long measurementId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Measurement measurement = measurementService.getMeasurementById(measurementId, username);
        return measurement;
    }

    @PutMapping("/{measurementId}")
    public Measurement updateMeasurement(@PathVariable Long measurementId,
            @RequestBody Measurement measurementDetails,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        return measurementService.updateMeasurement(measurementId, measurementDetails, username);

    }

    @DeleteMapping("/{measurementId}")
    public void deleteMeasurement(@PathVariable Long measurementId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        measurementService.deleteMeasurement(measurementId, username);
    }
}
