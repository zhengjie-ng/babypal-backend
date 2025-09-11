package com.babypal.controllers;

import com.babypal.dtos.UserDTO;
import com.babypal.models.Baby;
import com.babypal.models.GrowthGuide;
import com.babypal.models.Log;
import com.babypal.models.Measurement;
import com.babypal.models.Record;
import com.babypal.models.Role;
import com.babypal.models.User;
// import com.babypal.repositories.GrowthGuideRepository;
import com.babypal.repositories.RoleRepository;
import com.babypal.services.BabyService;
import com.babypal.services.GrowthGuideService;
import com.babypal.services.LogService;
import com.babypal.services.MeasurementService;
import com.babypal.services.RecordService;
import com.babypal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BabyService babyService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private GrowthGuideService growthGuideService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LogService logService;

    // @Autowired
    // private GrowthGuideRepository growthGuideRepository;

    @GetMapping("/get-users")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PutMapping("/update-role")
    public ResponseEntity<String> updateUserRole(@RequestParam Long userId, 
                                                 @RequestParam String roleName,
                                                 @AuthenticationPrincipal UserDetails adminDetails) {
        userService.updateUserRole(userId, roleName);
        
        // Log admin action
        User admin = userService.findByUsername(adminDetails.getUsername());
        logService.logAdminAction(adminDetails.getUsername(), admin.getUserId(), 
                                "UPDATE_USER_ROLE", "USER", userId);
        
        return ResponseEntity.ok("User role updated");
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PutMapping("/update-lock-status")
    public ResponseEntity<String> updateAccountLockStatus(@RequestParam Long userId, @RequestParam boolean lock,
                                                         @AuthenticationPrincipal UserDetails adminDetails) {
        userService.updateAccountLockStatus(userId, lock);
        
        // Log admin action
        User admin = userService.findByUsername(adminDetails.getUsername());
        logService.logAdminAction(adminDetails.getUsername(), admin.getUserId(), 
                                "UPDATE_LOCK_STATUS", "USER", userId);
        
        return ResponseEntity.ok("Account lock status updated");
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @PutMapping("/update-expiry-status")
    public ResponseEntity<String> updateAccountExpiryStatus(@RequestParam Long userId, @RequestParam boolean expire) {
        userService.updateAccountExpiryStatus(userId, expire);
        return ResponseEntity.ok("Account expiry status updated");
    }

    @PutMapping("/update-enabled-status")
    public ResponseEntity<String> updateAccountEnabledStatus(@RequestParam Long userId, @RequestParam boolean enabled,
                                                             @AuthenticationPrincipal UserDetails adminDetails) {
        userService.updateAccountEnabledStatus(userId, enabled);
        
        // Log admin action
        User admin = userService.findByUsername(adminDetails.getUsername());
        logService.logAdminAction(adminDetails.getUsername(), admin.getUserId(), 
                                "UPDATE_ENABLED_STATUS", "USER", userId);
        
        return ResponseEntity.ok("Account enabled status updated");
    }

    @PutMapping("/update-credentials-expiry-status")
    public ResponseEntity<String> updateCredentialsExpiryStatus(@RequestParam Long userId, @RequestParam boolean expire) {
        userService.updateCredentialsExpiryStatus(userId, expire);
        return ResponseEntity.ok("Credentials expiry status updated");
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam Long userId, @RequestParam String password) {
        try {
            userService.updatePassword(userId, password);
            return ResponseEntity.ok("Password updated");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/get-babies")
    public ResponseEntity<List<Baby>> getAllBabies() {
        return new ResponseEntity<>(babyService.getAllBabies(), HttpStatus.OK);
    }

    @GetMapping("/get-records")
    public ResponseEntity<List<Record>> getAllRecords() {
        return new ResponseEntity<>(recordService.getAllRecords(), HttpStatus.OK);
    }

    @GetMapping("/get-measurements")
    public ResponseEntity<List<Measurement>> getAllMeasurements() {
        return new ResponseEntity<>(measurementService.getAllMeasurements(), HttpStatus.OK);
    }

    @PutMapping("/{growthGuideId}")
    public GrowthGuide updateGrowthGuide(@PathVariable Long growthGuideId, @RequestBody GrowthGuide growthGuideDetails) {
        return growthGuideService.updateGrowthGuide(growthGuideId, growthGuideDetails);
    }

    @PutMapping("/update-account-expiry-date")
    public ResponseEntity<String> updateAccountExpiryDate(@RequestParam Long userId, 
                                                         @RequestParam String expiryDate) {
        try {
            LocalDate date = LocalDate.parse(expiryDate);
            userService.updateAccountExpiryDate(userId, date);
            return ResponseEntity.ok("Account expiry date updated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format or user not found");
        }
    }

    @PutMapping("/update-credentials-expiry-date")
    public ResponseEntity<String> updateCredentialsExpiryDate(@RequestParam Long userId, 
                                                             @RequestParam String expiryDate) {
        try {
            LocalDate date = LocalDate.parse(expiryDate);
            userService.updateCredentialsExpiryDate(userId, date);
            return ResponseEntity.ok("Credentials expiry date updated");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format or user not found");
        }
    }

    @PutMapping("/update-email")
    public ResponseEntity<String> updateEmail(@RequestParam Long userId, 
                                             @RequestParam String email) {
        try {
            userService.updateEmail(userId, email);
            return ResponseEntity.ok("Email address updated");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<List<Log>> getAllLogs() {
        return new ResponseEntity<>(logService.getAllLogs(), HttpStatus.OK);
    }

    @GetMapping("/logs/{logId}")
    public ResponseEntity<Log> getLogById(@PathVariable Long logId) {
        try {
            Log log = logService.getLogById(logId, "admin");
            return new ResponseEntity<>(log, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
