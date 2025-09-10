package com.babypal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.babypal.models.GrowthGuide;

public interface GrowthGuideRepository extends JpaRepository<GrowthGuide, Long>{
    
}
