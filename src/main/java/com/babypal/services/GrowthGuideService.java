package com.babypal.services;

import java.util.List;

import com.babypal.models.GrowthGuide;

public interface GrowthGuideService {
    GrowthGuide updateGrowthGuide(Long id, GrowthGuide growthGuideDetails);

    GrowthGuide getGrowthGuideById(Long id);

    List<GrowthGuide> getAllGrowthGuides();
}
