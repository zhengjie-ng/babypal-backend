package com.babypal.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.babypal.exceptions.UnauthorizedAccessException;
import com.babypal.models.GrowthGuide;
import com.babypal.repositories.GrowthGuideRepository;
import com.babypal.services.GrowthGuideService;

@Service
public class GrowthGuideServiceImpl implements GrowthGuideService {
    private final GrowthGuideRepository growthGuideRepository;

    @Autowired
    public GrowthGuideServiceImpl(GrowthGuideRepository growthGuideRepository) {
        this.growthGuideRepository = growthGuideRepository;
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public GrowthGuide updateGrowthGuide(Long id, GrowthGuide growthGuideDetails) {
        GrowthGuide existingGuide = growthGuideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GrowthGuide not found with id: " + id));
        if (!isAdmin()) {
            throw new UnauthorizedAccessException("Only admins can update growth guides");
        }
        // Update the existing guide with new details
        existingGuide.setMonthRange(growthGuideDetails.getMonthRange());
        existingGuide.setAgeDescription(growthGuideDetails.getAgeDescription());
        existingGuide.setPhysicalDevelopment(growthGuideDetails.getPhysicalDevelopment());
        existingGuide.setCognitiveSocial(growthGuideDetails.getCognitiveSocial());
        existingGuide.setMotorSkills(growthGuideDetails.getMotorSkills());
        return growthGuideRepository.save(existingGuide);
    }

    @Override
    public GrowthGuide getGrowthGuideById(Long id) {
        GrowthGuide guide = growthGuideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GrowthGuide not found with id: " + id));
        return guide;
    }

    @Override
    public List<GrowthGuide> getAllGrowthGuides() {
        return growthGuideRepository.findAll();
    }
}
