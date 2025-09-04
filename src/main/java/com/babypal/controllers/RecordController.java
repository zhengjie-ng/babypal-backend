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

import com.babypal.models.Record;
import com.babypal.services.RecordService;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public Record createRecord(@RequestBody Record record, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return recordService.createRecord(record, username);

    }

    @GetMapping
    public List<Record> getRecords(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return recordService.getAllRecordsByUsername(username);
        // return recordService.getAllRecordsByBabyId(null, username);
    }

    @GetMapping("/{recordId}")
    public Record getRecordById(@PathVariable Long recordId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Record record = recordService.getRecordById(recordId, username);
        return record;
    }

    @PutMapping("/{recordId}")
    public Record updateRecord(@PathVariable Long recordId,
            @RequestBody Record recordDetails,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        return recordService.updateRecord(recordId, recordDetails, username);

    }

    @DeleteMapping("/{recordId}")
    public void deleteRecord(@PathVariable Long recordId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        recordService.deleteRecord(recordId, username);
    }
}
