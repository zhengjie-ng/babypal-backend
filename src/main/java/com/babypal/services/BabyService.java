package com.babypal.services;

import java.util.List;

import com.babypal.models.Baby;

public interface BabyService {
    Baby createBaby(String username, Baby baby);

    Baby updateBaby(Long babyId, Baby babyDetails, String username);

    void deleteBaby(Long babyId, String username);

    List<Baby> getAllBabiesByUsername(String username);

    Baby getBabyById(Long babyId, String username);

    List<Baby> getAllBabies();
    
}
