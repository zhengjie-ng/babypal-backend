package com.babypal.services;

import java.util.List;

import com.babypal.models.Record;

public interface RecordService {
    Record createRecord(Record record, String username);

    Record updateRecord(Long recordId, Record recordDetails, String username);

    void deleteRecord(Long recordId, String username);

    Record getRecordById(Long record, String username);

    List<Record> getAllRecordsByUsername(String username);

    List<Record> getAllRecords();
    
}
