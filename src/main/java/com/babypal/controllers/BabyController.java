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

import com.babypal.models.Baby;
import com.babypal.services.BabyService;

@RestController
@RequestMapping("/api/babies")
public class BabyController {

    private final BabyService babyService;

    public BabyController(BabyService babyService) {
        this.babyService = babyService;
    }

    @PostMapping
    public Baby createBaby(@RequestBody Baby baby, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return babyService.createBaby(username, baby);

    }

    @GetMapping
    public List<Baby> getBabies(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return babyService.getAllBabiesByUsername(username);
    }

    @GetMapping("/{babyId}")
    public Baby getBabyById(@PathVariable Long babyId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Baby baby = babyService.getBabyById(babyId, username);
        return baby;
    }

    @PutMapping("/{babyId}")
    public Baby updateBaby(@PathVariable Long babyId,
            @RequestBody Baby babyDetails,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        return babyService.updateBaby(babyId, babyDetails, username);

    }

    @DeleteMapping("/{babyId}")
    public void deleteBaby(@PathVariable Long babyId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        babyService.deleteBaby(babyId, username);
    }
}
