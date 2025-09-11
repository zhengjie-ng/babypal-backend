package com.babypal.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.babypal.models.Log;
import com.babypal.repositories.LogRepository;
import com.babypal.services.LogService;

@Service
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;

    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // @Override
    // public Log createLog(String username, Log log) {
    //     Log newLog = new Log();
    //     newLog.setUsername(username);
    //     newLog.setType(log.getType());
    //     newLog.setTypeId(log.getTypeId());
    //     newLog.setAction(log.getAction());
    //     newLog.setStatusCode(log.getStatusCode());
    //     return logRepository.save(newLog);
    // }

    @Override
    public Log getLogById(Long logId, String username) {
        return logRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found with id: " + logId));
    }

    @Override
    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }
    
    @Override
    public Log logSignInSuccess(String username, Long userId) {
        Log log = Log.builder()
                .username(username)
                .type("AUTH")
                .typeId(userId)
                .action("SIGN_IN")
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logSignInFailure(String username, Long userId) {
        Log log = Log.builder()
                .username(username != null ? username : "UNKNOWN")
                .type("AUTH")
                .typeId(userId)
                .action("SIGN_IN_FAILED")
                .statusCode("401")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logSignOut(String username, Long userId) {
        Log log = Log.builder()
                .username(username)
                .type("AUTH")
                .typeId(userId)
                .action("SIGN_OUT")
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logSignUp(String username, Long userId) {
        Log log = Log.builder()
                .username(username)
                .type("AUTH")
                .typeId(userId)
                .action("SIGN_UP")
                .statusCode("201")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logEntityCreate(String username, Long userId, String entityType, Long entityId, String action) {
        Log log = Log.builder()
                .username(username)
                .type(entityType)
                .typeId(entityId)
                .action(action)
                .statusCode("201")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logEntityUpdate(String username, Long userId, String entityType, Long entityId, String action) {
        Log log = Log.builder()
                .username(username)
                .type(entityType)
                .typeId(entityId)
                .action(action)
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logEntityDelete(String username, Long userId, String entityType, Long entityId, String action) {
        Log log = Log.builder()
                .username(username)
                .type(entityType)
                .typeId(entityId)
                .action(action)
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logEntityRead(String username, Long userId, String entityType, Long entityId, String action) {
        Log log = Log.builder()
                .username(username)
                .type(entityType)
                .typeId(entityId)
                .action(action)
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logAdminAction(String adminUsername, Long adminUserId, String action, String targetType, Long targetId) {
        Log log = Log.builder()
                .username(adminUsername)
                .type("ADMIN")
                .typeId(targetId)
                .action(action)
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logCredentialsUpdate(String username, Long userId) {
        Log log = Log.builder()
                .username(username)
                .type("AUTH")
                .typeId(userId)
                .action("CREDENTIALS_UPDATE")
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logTwoFactorEnable(String username, Long userId) {
        Log log = Log.builder()
                .username(username)
                .type("AUTH")
                .typeId(userId)
                .action("TWO_FACTOR_ENABLE")
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logTwoFactorDisable(String username, Long userId) {
        Log log = Log.builder()
                .username(username)
                .type("AUTH")
                .typeId(userId)
                .action("TWO_FACTOR_DISABLE")
                .statusCode("200")
                .build();
        return logRepository.save(log);
    }
    
    @Override
    public Log logTwoFactorVerification(String username, Long userId, boolean success) {
        Log log = Log.builder()
                .username(username)
                .type("AUTH")
                .typeId(userId)
                .action(success ? "TWO_FACTOR_VERIFY_SUCCESS" : "TWO_FACTOR_VERIFY_FAILED")
                .statusCode(success ? "200" : "401")
                .build();
        return logRepository.save(log);
    }
}
