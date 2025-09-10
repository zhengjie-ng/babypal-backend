package com.babypal.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.babypal.models.GrowthGuide;
import com.babypal.repositories.GrowthGuideRepository;
import com.babypal.services.GrowthGuideService;

@RestController
@RequestMapping("/api/growth-guides")
public class GrowthGuideController {
    private final GrowthGuideService growthGuideService;
    private final GrowthGuideRepository growthGuideRepository;

    public GrowthGuideController(GrowthGuideService growthGuideService, GrowthGuideRepository growthGuideRepository) {
        this.growthGuideService = growthGuideService;
        this.growthGuideRepository = growthGuideRepository;
    }

    @GetMapping("/{growthGuideId}")
    public GrowthGuide getGrowthGuideById(@PathVariable Long growthGuideId) {
        return growthGuideService.getGrowthGuideById(growthGuideId);
    }

    @GetMapping
    public ResponseEntity<List<GrowthGuide>> getAllGrowthGuides() {
        return new ResponseEntity<>(growthGuideRepository.findAll(), HttpStatus.OK);
    }

}
