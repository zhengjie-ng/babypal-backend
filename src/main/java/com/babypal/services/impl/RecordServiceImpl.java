package com.babypal.services.impl;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.babypal.exceptions.UnauthorizedAccessException;
import com.babypal.models.Baby;
import com.babypal.models.Record;
import com.babypal.models.User;
import com.babypal.repositories.RecordRepository;
import com.babypal.repositories.BabyRepository;
import com.babypal.services.LogService;
import com.babypal.services.RecordService;
import com.babypal.services.UserService;

@Service
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final BabyRepository babyRepository;
    private final LogService logService;
    private final UserService userService;

    public RecordServiceImpl(RecordRepository recordRepository, BabyRepository babyRepository, LogService logService, UserService userService) {
        this.recordRepository = recordRepository;
        this.babyRepository = babyRepository;
        this.logService = logService;
        this.userService = userService;
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public Record createRecord(Record record, String username) {
        Baby baby = babyRepository.findById(record.getBaby().getId())
                .orElseThrow(() -> new RuntimeException("Baby not found"));

        if (!isAdmin() && !baby.getCaregivers().contains(username)) {
            throw new UnauthorizedAccessException("Only caregivers can create records");
        }

        record.setBaby(baby);  // Set the fully loaded baby object
        record.setAuthor(username);
        Record savedRecord = recordRepository.save(record);
        
        // Log record creation
        User user = userService.findByUsername(username);
        logService.logEntityCreate(username, user.getUserId(), "RECORD", savedRecord.getId(), "CREATE_RECORD");
        
        return savedRecord;
    }

    @Override
    public Record updateRecord(Long recordId, Record recordDetails, String username) {
        Record existingRecord = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + recordId));

        if (!isAdmin() && !existingRecord.getAuthor().equals(username)) {
            throw new UnauthorizedAccessException("Only admins and the author can update this record");
        }

        existingRecord.setType(recordDetails.getType());
        existingRecord.setSubType(recordDetails.getSubType());
        existingRecord.setNote(recordDetails.getNote());
        existingRecord.setStartTime(recordDetails.getStartTime());
        existingRecord.setEndTime(recordDetails.getEndTime());

        Record updatedRecord = recordRepository.save(existingRecord);
        
        // Log record update
        User user = userService.findByUsername(username);
        logService.logEntityUpdate(username, user.getUserId(), "RECORD", recordId, "UPDATE_RECORD");
        
        return updatedRecord;
    }

    @Override
    public void deleteRecord(Long recordId, String username) {
        Record existingRecord = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + recordId));

        if (!isAdmin() && !existingRecord.getAuthor().equals(username)) {
            throw new UnauthorizedAccessException("Only admins and the author can delete this record");
        }

        recordRepository.delete(existingRecord);
        
        // Log record deletion
        User user = userService.findByUsername(username);
        logService.logEntityDelete(username, user.getUserId(), "RECORD", recordId, "DELETE_RECORD");
    }

    @Override
    public Record getRecordById(Long record, String username) {
        Record existingRecord = recordRepository.findById(record)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + record));

        // Log record read
        User user = userService.findByUsername(username);
        logService.logEntityRead(username, user.getUserId(), "RECORD", record, "GET_RECORD");

        return existingRecord;
    }

    @Override
    public List<Record> getAllRecordsByUsername(String username) {
        return recordRepository.findByAuthor(username);
    }

    @Override
    public List<Record> getAllRecords() {
        return recordRepository.findAll();
    }
}
