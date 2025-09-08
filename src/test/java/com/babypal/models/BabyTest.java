package com.babypal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Baby Model Tests")
class BabyTest {

    private Validator validator;
    private Baby baby;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        baby = Baby.builder()
                .name("Emma Johnson")
                .gender("Female")
                .dateOfBirth(LocalDateTime.of(2023, 6, 15, 10, 30))
                .weight(3.2)
                .height(50.0)
                .headCircumference(35.0)
                .caregivers(Arrays.asList("John Johnson", "Jane Johnson"))
                .owner("john.johnson@example.com")
                .build();
    }

    @Test
    @DisplayName("Should create Baby with valid data")
    void shouldCreateBabyWithValidData() {
        assertNotNull(baby);
        assertEquals("Emma Johnson", baby.getName());
        assertEquals("Female", baby.getGender());
        assertEquals(LocalDateTime.of(2023, 6, 15, 10, 30), baby.getDateOfBirth());
        assertEquals(3.2, baby.getWeight());
        assertEquals(50.0, baby.getHeight());
        assertEquals(35.0, baby.getHeadCircumference());
        assertEquals(2, baby.getCaregivers().size());
        assertEquals("john.johnson@example.com", baby.getOwner());
    }

    @Test
    @DisplayName("Should pass validation with valid Baby data")
    void shouldPassValidationWithValidData() {
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        baby.setName("");
        
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertFalse(violations.isEmpty());
        
        ConstraintViolation<Baby> violation = violations.iterator().next();
        assertEquals("Baby name is required", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when name is null")
    void shouldFailValidationWhenNameIsNull() {
        baby.setName(null);
        
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertFalse(violations.isEmpty());
        
        ConstraintViolation<Baby> violation = violations.iterator().next();
        assertEquals("Baby name is required", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when name is too short")
    void shouldFailValidationWhenNameIsTooShort() {
        baby.setName("Em");
        
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertFalse(violations.isEmpty());
        
        ConstraintViolation<Baby> violation = violations.iterator().next();
        assertEquals("Baby name must be between 3 and 100 characters", violation.getMessage());
    }

    @Test
    @DisplayName("Should fail validation when name is too long")
    void shouldFailValidationWhenNameIsTooLong() {
        String longName = "A".repeat(101);
        baby.setName(longName);
        
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertFalse(violations.isEmpty());
        
        ConstraintViolation<Baby> violation = violations.iterator().next();
        assertEquals("Baby name must be between 3 and 100 characters", violation.getMessage());
    }

    @Test
    @DisplayName("Should accept valid name at boundary lengths")
    void shouldAcceptValidNameAtBoundaryLengths() {
        // Test minimum length (3 characters)
        baby.setName("Amy");
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertTrue(violations.isEmpty());

        // Test maximum length (100 characters)
        String maxName = "A".repeat(100);
        baby.setName(maxName);
        violations = validator.validate(baby);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle null caregivers list")
    void shouldHandleNullCaregiversList() {
        baby.setCaregivers(null);
        
        assertNull(baby.getCaregivers());
        // Should not cause validation errors as caregivers is not required
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle empty caregivers list")
    void shouldHandleEmptyCaregiversList() {
        baby.setCaregivers(Arrays.asList());
        
        assertTrue(baby.getCaregivers().isEmpty());
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should set timestamps on creation")
    void shouldSetTimestampsOnCreation() {
        Baby newBaby = new Baby();
        newBaby.setName("Test Baby");
        
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        newBaby.onCreate();
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);
        
        assertNotNull(newBaby.getCreatedAt());
        assertNotNull(newBaby.getUpdatedAt());
        assertTrue(newBaby.getCreatedAt().isAfter(beforeCreate));
        assertTrue(newBaby.getCreatedAt().isBefore(afterCreate));
        assertEquals(newBaby.getCreatedAt(), newBaby.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set default dateOfBirth on creation if null")
    void shouldSetDefaultDateOfBirthOnCreationIfNull() {
        Baby newBaby = new Baby();
        newBaby.setName("Test Baby");
        newBaby.setDateOfBirth(null);
        
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        newBaby.onCreate();
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);
        
        assertNotNull(newBaby.getDateOfBirth());
        assertTrue(newBaby.getDateOfBirth().isAfter(beforeCreate));
        assertTrue(newBaby.getDateOfBirth().isBefore(afterCreate));
    }

    @Test
    @DisplayName("Should not override existing dateOfBirth on creation")
    void shouldNotOverrideExistingDateOfBirthOnCreation() {
        LocalDateTime originalDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        baby.setDateOfBirth(originalDate);
        
        baby.onCreate();
        
        assertEquals(originalDate, baby.getDateOfBirth());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void shouldUpdateTimestampOnUpdate() {
        baby.onCreate();
        LocalDateTime originalUpdatedAt = baby.getUpdatedAt();
        
        try {
            Thread.sleep(10); // Ensure time difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        baby.onUpdate();
        
        assertNotEquals(originalUpdatedAt, baby.getUpdatedAt());
        assertTrue(baby.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should support Builder pattern")
    void shouldSupportBuilderPattern() {
        LocalDateTime birthDate = LocalDateTime.of(2023, 12, 1, 14, 30);
        List<String> caregivers = Arrays.asList("Parent1", "Parent2");
        
        Baby builtBaby = Baby.builder()
                .name("Builder Baby")
                .gender("Male")
                .dateOfBirth(birthDate)
                .weight(3.5)
                .height(52.0)
                .headCircumference(36.0)
                .caregivers(caregivers)
                .owner("parent@example.com")
                .build();
        
        assertEquals("Builder Baby", builtBaby.getName());
        assertEquals("Male", builtBaby.getGender());
        assertEquals(birthDate, builtBaby.getDateOfBirth());
        assertEquals(3.5, builtBaby.getWeight());
        assertEquals(52.0, builtBaby.getHeight());
        assertEquals(36.0, builtBaby.getHeadCircumference());
        assertEquals(caregivers, builtBaby.getCaregivers());
        assertEquals("parent@example.com", builtBaby.getOwner());
    }

    @Test
    @DisplayName("Should handle numeric measurements with null values")
    void shouldHandleNumericMeasurementsWithNullValues() {
        baby.setWeight(null);
        baby.setHeight(null);
        baby.setHeadCircumference(null);
        
        assertNull(baby.getWeight());
        assertNull(baby.getHeight());
        assertNull(baby.getHeadCircumference());
        
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle numeric measurements with zero values")
    void shouldHandleNumericMeasurementsWithZeroValues() {
        baby.setWeight(0.0);
        baby.setHeight(0.0);
        baby.setHeadCircumference(0.0);
        
        assertEquals(0.0, baby.getWeight());
        assertEquals(0.0, baby.getHeight());
        assertEquals(0.0, baby.getHeadCircumference());
        
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should handle negative numeric measurements")
    void shouldHandleNegativeNumericMeasurements() {
        baby.setWeight(-1.0);
        baby.setHeight(-1.0);
        baby.setHeadCircumference(-1.0);
        
        assertEquals(-1.0, baby.getWeight());
        assertEquals(-1.0, baby.getHeight());
        assertEquals(-1.0, baby.getHeadCircumference());
        
        // No validation constraints prevent negative values in current model
        Set<ConstraintViolation<Baby>> violations = validator.validate(baby);
        assertTrue(violations.isEmpty());
    }
}