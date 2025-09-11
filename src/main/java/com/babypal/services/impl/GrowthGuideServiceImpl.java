package com.babypal.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.babypal.exceptions.UnauthorizedAccessException;
import com.babypal.models.GrowthGuide;
import com.babypal.models.User;
import com.babypal.repositories.GrowthGuideRepository;
import com.babypal.services.GrowthGuideService;
import com.babypal.services.LogService;
import com.babypal.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class GrowthGuideServiceImpl implements GrowthGuideService {
    private final GrowthGuideRepository growthGuideRepository;
    private final LogService logService;
    private final UserService userService;

    @Autowired
    public GrowthGuideServiceImpl(GrowthGuideRepository growthGuideRepository, LogService logService, UserService userService) {
        this.growthGuideRepository = growthGuideRepository;
        this.logService = logService;
        this.userService = userService;
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
        
        GrowthGuide updatedGuide = growthGuideRepository.save(existingGuide);
        
        // Log growth guide update
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        logService.logEntityUpdate(userDetails.getUsername(), user.getUserId(), "GROWTH_GUIDE", id, "UPDATE_GROWTH_GUIDE");
        
        return updatedGuide;
    }

    @Override
    public GrowthGuide getGrowthGuideById(Long id) {
        GrowthGuide guide = growthGuideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GrowthGuide not found with id: " + id));
        
        // Log growth guide read (only if user is authenticated)
        if (SecurityContextHolder.getContext().getAuthentication() != null && 
            SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            logService.logEntityRead(userDetails.getUsername(), user.getUserId(), "GROWTH_GUIDE", id, "GET_GROWTH_GUIDE");
        }
        
        return guide;
    }

    @Override
    public List<GrowthGuide> getAllGrowthGuides() {
        return growthGuideRepository.findAll();
    }
}
