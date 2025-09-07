package com.babypal.services;

import com.babypal.dtos.UserDTO;
import com.babypal.models.User;

import java.util.List;

public interface UserService {
    void updateUserRole(Long userId, String roleName);

    List<User> getAllUsers();

    UserDTO getUserById(Long id);

    User findByUsername(String username);

    void generatePasswordResetToken(String email);

    void resetPassword(String token, String newPassword);

    void updateAccountLockStatus(Long userId, boolean lock);

    void updateAccountExpiryStatus(Long userId, boolean expire);

    void updateAccountEnabledStatus(Long userId, boolean enabled);

    void updateCredentialsExpiryStatus(Long userId, boolean expire);

    void updatePassword(Long userId, String password);

    boolean checkUserExists(String username);

    void updateAccountExpiryDate(Long userId, java.time.LocalDate expiryDate);

    void updateCredentialsExpiryDate(Long userId, java.time.LocalDate expiryDate);

    void updateEmail(Long userId, String email);
}

