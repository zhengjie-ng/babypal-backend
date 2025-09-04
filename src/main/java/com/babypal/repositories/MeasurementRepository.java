package com.babypal.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.babypal.models.Measurement;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    List<Measurement> findByAuthor(String username);
    
}
