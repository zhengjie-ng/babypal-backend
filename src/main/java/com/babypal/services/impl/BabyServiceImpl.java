package com.babypal.services.impl;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.babypal.exceptions.UnauthorizedAccessException;
import com.babypal.models.Baby;
import com.babypal.repositories.BabyRepository;
import com.babypal.services.BabyService;

@Service
public class BabyServiceImpl implements BabyService {
    private final BabyRepository babyRepository;

    public BabyServiceImpl(BabyRepository babyRepository) {
        this.babyRepository = babyRepository;
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public Baby createBaby(String username, Baby baby) {
        Baby newBaby = new Baby();
        newBaby.setName(baby.getName());
        newBaby.setGender(baby.getGender());
        newBaby.setDateOfBirth(baby.getDateOfBirth());
        newBaby.setWeight(baby.getWeight());
        newBaby.setHeight(baby.getHeight());
        newBaby.setHeadCircumference(baby.getHeadCircumference());
        newBaby.setCaregivers(List.of(username));
        newBaby.setOwner(username);

        return babyRepository.save(newBaby);
    }

    @Override
    public Baby updateBaby(Long babyId, Baby babyDetails, String username) {
        Baby existingBaby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Baby not found with id: " + babyId));

        if (!isAdmin() && !existingBaby.getCaregivers().contains(username)) {
            throw new UnauthorizedAccessException("Only admins and caregivers can update baby details");
        }

        existingBaby.setName(babyDetails.getName());
        existingBaby.setGender(babyDetails.getGender());
        existingBaby.setDateOfBirth(babyDetails.getDateOfBirth());
        existingBaby.setWeight(babyDetails.getWeight());
        existingBaby.setHeight(babyDetails.getHeight());
        existingBaby.setHeadCircumference(babyDetails.getHeadCircumference());
        existingBaby.setCaregivers(babyDetails.getCaregivers());
        existingBaby.setOwner(babyDetails.getOwner());
        return babyRepository.save(existingBaby);
    }

    @Override
    public void deleteBaby(Long babyId, String username) {
        Baby existingBaby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Baby not found with id: " + babyId));

        if (!isAdmin() && !existingBaby.getOwner().equals(username)) {
            throw new UnauthorizedAccessException("Only admins and owners can delete baby profiles");
        }
        babyRepository.delete(existingBaby);
    }

    @Override
    public List<Baby> getAllBabiesByUsername(String username) {
        return babyRepository.findAll().stream()
                .filter(baby -> baby.getOwner().equals(username)
                        || (baby.getCaregivers() != null && baby.getCaregivers().contains(username)))
                .toList();
    }

    @Override
    public Baby getBabyById(Long babyId, String username) {
        Baby existingBaby = babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Baby not found with id: " + babyId));

        if (!isAdmin() && !existingBaby.getOwner().equals(username)) {
            throw new UnauthorizedAccessException("Only admins and caregivers can see selected baby profile");
        }

        return babyRepository.findById(babyId)
                .orElseThrow(() -> new RuntimeException("Baby not found with id: " + babyId));
    }
    
    @Override
    public List<Baby> getAllBabies() {
        return babyRepository.findAll();
    }
        

}
