package com.babypal.services.impl;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.babypal.exceptions.UnauthorizedAccessException;
import com.babypal.models.Baby;
import com.babypal.models.Record;
import com.babypal.repositories.RecordRepository;
import com.babypal.repositories.BabyRepository;
import com.babypal.services.RecordService;

@Service
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final BabyRepository babyRepository; // Add this field

    // Update constructor to include BabyRepository
    public RecordServiceImpl(RecordRepository recordRepository, BabyRepository babyRepository) {
        this.recordRepository = recordRepository;
        this.babyRepository = babyRepository;
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
        return recordRepository.save(record);
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

        return recordRepository.save(existingRecord);
    }

    @Override
    public void deleteRecord(Long recordId, String username) {
        Record existingRecord = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + recordId));

        if (!isAdmin() && !existingRecord.getAuthor().equals(username)) {
            throw new UnauthorizedAccessException("Only admins and the author can delete this record");
        }

        recordRepository.delete(existingRecord);
    }

    @Override
    public Record getRecordById(Long record, String username) {
        Record existingRecord = recordRepository.findById(record)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + record));

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
