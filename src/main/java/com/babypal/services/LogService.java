package com.babypal.services;

import java.util.List;

import com.babypal.models.Log;

public interface LogService {
    // Log createLog(String username, Log log);

    Log getLogById(Long logId, String username);

    List<Log> getAllLogs();
    
    Log logSignInSuccess(String username, Long userId);
    
    Log logSignInFailure(String username, Long userId);
    
    Log logSignOut(String username, Long userId);
    
    Log logSignUp(String username, Long userId);
    
    // Entity operation logging methods
    Log logEntityCreate(String username, Long userId, String entityType, Long entityId, String action);
    
    Log logEntityUpdate(String username, Long userId, String entityType, Long entityId, String action);
    
    Log logEntityDelete(String username, Long userId, String entityType, Long entityId, String action);
    
    Log logEntityRead(String username, Long userId, String entityType, Long entityId, String action);
    
    // Admin action logging
    Log logAdminAction(String adminUsername, Long adminUserId, String action, String targetType, Long targetId);
    
    // Credential update logging
    Log logCredentialsUpdate(String username, Long userId);
    
    // 2FA activity logging
    Log logTwoFactorEnable(String username, Long userId);
    
    Log logTwoFactorDisable(String username, Long userId);
    
    Log logTwoFactorVerification(String username, Long userId, boolean success);
}
