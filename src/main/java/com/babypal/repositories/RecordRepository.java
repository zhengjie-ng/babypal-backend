package com.babypal.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.babypal.models.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByAuthor(String username);
    
}
