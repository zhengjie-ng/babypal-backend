package com.babypal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Record Model Tests")
class RecordTest {

    private Validator validator;
    private Record record;
    private Baby baby;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        baby = Baby.builder()
                .id(1L)
                .name("Test Baby")
                .build();
        
        record = Record.builder()
                .author("John Doe")
                .type("feeding")
                .subType("bottle")
                .note("Baby had 120ml formula")
                .startTime(LocalDateTime.of(2024, 1, 15, 10, 30))
                .endTime(LocalDateTime.of(2024, 1, 15, 10, 45))
                .baby(baby)
                .build();
    }

    @Test
    @DisplayName("Should create Record with valid data")
    void shouldCreateRecordWithValidData() {
        assertNotNull(record);
        assertEquals("John Doe", record.getAuthor());
        assertEquals("feeding", record.getType());
        assertEquals("bottle", record.getSubType());
        assertEquals("Baby had 120ml formula", record.getNote());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), record.getStartTime());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 45), record.getEndTime());
        assertEquals(baby, record.getBaby());
    }

    @Test
    @DisplayName("Should pass validation with valid Record data")
    void shouldPassValidationWithValidData() {
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when type is blank")
    void shouldFailValidationWhenTypeIsBlank() {
        record.setType("");
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertFalse(violations.isEmpty());
        
        ConstraintViolation<Record> violation = violations.iterator().next();
        assertEquals("Record type is required", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when type is null")
    void shouldFailValidationWhenTypeIsNull() {
        record.setType(null);
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertFalse(violations.isEmpty());
        
        ConstraintViolation<Record> violation = violations.iterator().next();
        assertEquals("Record type is required", violation.getMessage());
    }

    @Test
    @DisplayName("Should allow null subType")
    void shouldAllowNullSubType() {
        record.setSubType(null);
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        assertNull(record.getSubType());
    }

    @Test
    @DisplayName("Should allow empty subType")
    void shouldAllowEmptySubType() {
        record.setSubType("");
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        assertEquals("", record.getSubType());
    }

    @Test
    @DisplayName("Should allow null note")
    void shouldAllowNullNote() {
        record.setNote(null);
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        assertNull(record.getNote());
    }

    @Test
    @DisplayName("Should allow empty note")
    void shouldAllowEmptyNote() {
        record.setNote("");
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        assertEquals("", record.getNote());
    }

    @Test
    @DisplayName("Should allow null author")
    void shouldAllowNullAuthor() {
        record.setAuthor(null);
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        assertNull(record.getAuthor());
    }

    @Test
    @DisplayName("Should set timestamps on creation")
    void shouldSetTimestampsOnCreation() {
        Record newRecord = new Record();
        newRecord.setType("test");
        newRecord.setBaby(baby);
        
        LocalDateTime beforeCreate = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().minusSeconds(1);
        newRecord.onCreate();
        LocalDateTime afterCreate = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().plusSeconds(1);
        
        assertNotNull(newRecord.getCreatedAt());
        assertNotNull(newRecord.getUpdatedAt());
        assertTrue(newRecord.getCreatedAt().isAfter(beforeCreate));
        assertTrue(newRecord.getCreatedAt().isBefore(afterCreate));
        assertEquals(newRecord.getCreatedAt(), newRecord.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set default startTime on creation if null")
    void shouldSetDefaultStartTimeOnCreationIfNull() {
        Record newRecord = new Record();
        newRecord.setType("test");
        newRecord.setBaby(baby);
        newRecord.setStartTime(null);
        
        LocalDateTime beforeCreate = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().minusSeconds(1);
        newRecord.onCreate();
        LocalDateTime afterCreate = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().plusSeconds(1);
        
        assertNotNull(newRecord.getStartTime());
        assertTrue(newRecord.getStartTime().isAfter(beforeCreate));
        assertTrue(newRecord.getStartTime().isBefore(afterCreate));
    }

    @Test
    @DisplayName("Should not override existing startTime on creation")
    void shouldNotOverrideExistingStartTimeOnCreation() {
        LocalDateTime originalStartTime = LocalDateTime.of(2024, 1, 1, 9, 0);
        record.setStartTime(originalStartTime);
        
        record.onCreate();
        
        assertEquals(originalStartTime, record.getStartTime());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void shouldUpdateTimestampOnUpdate() {
        record.onCreate();
        LocalDateTime originalUpdatedAt = record.getUpdatedAt();
        
        try {
            Thread.sleep(10); // Ensure time difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        record.onUpdate();
        
        assertNotEquals(originalUpdatedAt, record.getUpdatedAt());
        assertTrue(record.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should support Builder pattern")
    void shouldSupportBuilderPattern() {
        LocalDateTime startTime = LocalDateTime.of(2024, 2, 1, 8, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 2, 1, 8, 30);
        
        Record builtRecord = Record.builder()
                .author("Jane Smith")
                .type("sleep")
                .subType("nap")
                .note("Short afternoon nap")
                .startTime(startTime)
                .endTime(endTime)
                .baby(baby)
                .build();
        
        assertEquals("Jane Smith", builtRecord.getAuthor());
        assertEquals("sleep", builtRecord.getType());
        assertEquals("nap", builtRecord.getSubType());
        assertEquals("Short afternoon nap", builtRecord.getNote());
        assertEquals(startTime, builtRecord.getStartTime());
        assertEquals(endTime, builtRecord.getEndTime());
        assertEquals(baby, builtRecord.getBaby());
    }

    @Test
    @DisplayName("Should handle null endTime")
    void shouldHandleNullEndTime() {
        record.setEndTime(null);
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        assertNull(record.getEndTime());
    }

    @Test
    @DisplayName("Should handle null startTime")
    void shouldHandleNullStartTime() {
        record.setStartTime(null);
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        assertNull(record.getStartTime());
    }

    @Test
    @DisplayName("Should handle endTime before startTime")
    void shouldHandleEndTimeBeforeStartTime() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 15, 10, 15); // Before start time
        
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        
        // No validation constraint prevents this in current model
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        
        assertTrue(record.getEndTime().isBefore(record.getStartTime()));
    }

    @Test
    @DisplayName("Should handle same startTime and endTime")
    void shouldHandleSameStartTimeAndEndTime() {
        LocalDateTime sameTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        
        record.setStartTime(sameTime);
        record.setEndTime(sameTime);
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        
        assertEquals(record.getStartTime(), record.getEndTime());
    }

    @Test
    @DisplayName("Should handle various record types")
    void shouldHandleVariousRecordTypes() {
        String[] recordTypes = {"feeding", "sleep", "diaper", "play", "bath", "medical"};
        
        for (String type : recordTypes) {
            record.setType(type);
            Set<ConstraintViolation<Record>> violations = validator.validate(record);
            assertTrue(violations.isEmpty(), "Should accept record type: " + type);
            assertEquals(type, record.getType());
        }
    }

    @Test
    @DisplayName("Should handle long note text")
    void shouldHandleLongNoteText() {
        String longNote = "A".repeat(1000);
        record.setNote(longNote);
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        assertEquals(longNote, record.getNote());
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void shouldHandleSpecialCharactersInTextFields() {
        record.setType("feeding & play");
        record.setSubType("bottle (formula) + vitamins");
        record.setNote("Baby ate 120ml @ 10:30am - very happy! 😊");
        record.setAuthor("Dr. María González-Smith");
        
        Set<ConstraintViolation<Record>> violations = validator.validate(record);
        assertTrue(violations.isEmpty());
        
        assertEquals("feeding & play", record.getType());
        assertEquals("bottle (formula) + vitamins", record.getSubType());
        assertEquals("Baby ate 120ml @ 10:30am - very happy! 😊", record.getNote());
        assertEquals("Dr. María González-Smith", record.getAuthor());
    }
}