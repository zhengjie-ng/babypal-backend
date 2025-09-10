package com.babypal.services.impl;

import com.babypal.dtos.UserDTO;
import com.babypal.models.AppRole;
import com.babypal.models.PasswordResetToken;
import com.babypal.models.Role;
import com.babypal.models.User;
import com.babypal.repositories.PasswordResetTokenRepository;
import com.babypal.repositories.RoleRepository;
import com.babypal.repositories.UserRepository;
import com.babypal.services.UserService;
import com.babypal.util.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    EmailService emailService;

    @Value("${frontend.urls}")
    private String[] frontendUrls;

    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDTO getUserById(Long id) {
        // return userRepository.findById(id).orElseThrow();
        User user = userRepository.findById(id).orElseThrow();
        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getTwoFactorSecret(),
                user.isTwoFactorEnabled(),
                user.getSignUpMethod(),
                user.getRole(),
                user.getCreatedDate(),
                user.getUpdatedDate());
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Not Found "));
        
                String token = UUID.randomUUID().toString();
                Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
                PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
                passwordResetTokenRepository.save(resetToken);

                String resetUrl = frontendUrls[0] + "/reset-password?token=" + token;
                // Send email to user
                emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired password reset token"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("This token has already been used.");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Password reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword)); 
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

    }

    @Override
    public void updatePassword(Long userId, String password) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password");
        }
    }


    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonLocked(!lock);
        userRepository.save(user);
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setCredentialsNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public boolean checkUserExists(String username) {
        return userRepository.existsByUserName(username);
    }

    @Override
    public void updateAccountExpiryDate(Long userId, java.time.LocalDate expiryDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("User not found"));
        user.setAccountExpiryDate(expiryDate);
        userRepository.save(user);
    }

    @Override
    public void updateCredentialsExpiryDate(Long userId, java.time.LocalDate expiryDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("User not found"));
        user.setCredentialsExpiryDate(expiryDate);
        userRepository.save(user);
    }

    @Override
    public void updateEmail(Long userId, String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email cannot be empty");
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RuntimeException("Invalid email format");
        }
        
        User user = userRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("User not found"));
        
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getUserId().equals(userId)) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setEmail(email);
        userRepository.save(user);
    }

}