package com.babypal.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Measurement Model Tests")
class MeasurementTest {

    private Validator validator;
    private Measurement measurement;
    private Baby baby;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        baby = Baby.builder()
                .id(1L)
                .name("Test Baby")
                .build();
        
        measurement = Measurement.builder()
                .author("Dr. Smith")
                .time(LocalDateTime.of(2024, 1, 15, 14, 30))
                .weight(4.2)
                .height(55.5)
                .headCircumference(38.0)
                .baby(baby)
                .build();
    }

    @Test
    @DisplayName("Should create Measurement with valid data")
    void shouldCreateMeasurementWithValidData() {
        assertNotNull(measurement);
        assertEquals("Dr. Smith", measurement.getAuthor());
        assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30), measurement.getTime());
        assertEquals(4.2, measurement.getWeight());
        assertEquals(55.5, measurement.getHeight());
        assertEquals(38.0, measurement.getHeadCircumference());
        assertEquals(baby, measurement.getBaby());
    }

    @Test
    @DisplayName("Should pass validation with valid Measurement data")
    void shouldPassValidationWithValidData() {
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should allow null author")
    void shouldAllowNullAuthor() {
        measurement.setAuthor(null);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertNull(measurement.getAuthor());
    }

    @Test
    @DisplayName("Should allow empty author")
    void shouldAllowEmptyAuthor() {
        measurement.setAuthor("");
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertEquals("", measurement.getAuthor());
    }

    @Test
    @DisplayName("Should allow null weight")
    void shouldAllowNullWeight() {
        measurement.setWeight(null);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertNull(measurement.getWeight());
    }

    @Test
    @DisplayName("Should allow null height")
    void shouldAllowNullHeight() {
        measurement.setHeight(null);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertNull(measurement.getHeight());
    }

    @Test
    @DisplayName("Should allow null head circumference")
    void shouldAllowNullHeadCircumference() {
        measurement.setHeadCircumference(null);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertNull(measurement.getHeadCircumference());
    }

    @Test
    @DisplayName("Should allow all measurements to be null")
    void shouldAllowAllMeasurementsToBeNull() {
        measurement.setWeight(null);
        measurement.setHeight(null);
        measurement.setHeadCircumference(null);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        
        assertNull(measurement.getWeight());
        assertNull(measurement.getHeight());
        assertNull(measurement.getHeadCircumference());
    }

    @Test
    @DisplayName("Should handle zero values for measurements")
    void shouldHandleZeroValuesForMeasurements() {
        measurement.setWeight(0.0);
        measurement.setHeight(0.0);
        measurement.setHeadCircumference(0.0);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        
        assertEquals(0.0, measurement.getWeight());
        assertEquals(0.0, measurement.getHeight());
        assertEquals(0.0, measurement.getHeadCircumference());
    }

    @Test
    @DisplayName("Should handle negative values for measurements")
    void shouldHandleNegativeValuesForMeasurements() {
        measurement.setWeight(-1.0);
        measurement.setHeight(-1.0);
        measurement.setHeadCircumference(-1.0);
        
        // No validation constraints prevent negative values in current model
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        
        assertEquals(-1.0, measurement.getWeight());
        assertEquals(-1.0, measurement.getHeight());
        assertEquals(-1.0, measurement.getHeadCircumference());
    }

    @Test
    @DisplayName("Should handle very large measurement values")
    void shouldHandleVeryLargeMeasurementValues() {
        measurement.setWeight(999.99);
        measurement.setHeight(999.99);
        measurement.setHeadCircumference(999.99);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        
        assertEquals(999.99, measurement.getWeight());
        assertEquals(999.99, measurement.getHeight());
        assertEquals(999.99, measurement.getHeadCircumference());
    }

    @Test
    @DisplayName("Should handle very small decimal values")
    void shouldHandleVerySmallDecimalValues() {
        measurement.setWeight(0.001);
        measurement.setHeight(0.001);
        measurement.setHeadCircumference(0.001);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        
        assertEquals(0.001, measurement.getWeight());
        assertEquals(0.001, measurement.getHeight());
        assertEquals(0.001, measurement.getHeadCircumference());
    }

    @Test
    @DisplayName("Should set timestamps on creation")
    void shouldSetTimestampsOnCreation() {
        Measurement newMeasurement = new Measurement();
        newMeasurement.setBaby(baby);
        
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        newMeasurement.onCreate();
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);
        
        assertNotNull(newMeasurement.getCreatedAt());
        assertNotNull(newMeasurement.getUpdatedAt());
        assertTrue(newMeasurement.getCreatedAt().isAfter(beforeCreate));
        assertTrue(newMeasurement.getCreatedAt().isBefore(afterCreate));
        assertEquals(newMeasurement.getCreatedAt(), newMeasurement.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set default time on creation if null")
    void shouldSetDefaultTimeOnCreationIfNull() {
        Measurement newMeasurement = new Measurement();
        newMeasurement.setBaby(baby);
        newMeasurement.setTime(null);
        
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        newMeasurement.onCreate();
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);
        
        assertNotNull(newMeasurement.getTime());
        assertTrue(newMeasurement.getTime().isAfter(beforeCreate));
        assertTrue(newMeasurement.getTime().isBefore(afterCreate));
    }

    @Test
    @DisplayName("Should not override existing time on creation")
    void shouldNotOverrideExistingTimeOnCreation() {
        LocalDateTime originalTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        measurement.setTime(originalTime);
        
        measurement.onCreate();
        
        assertEquals(originalTime, measurement.getTime());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void shouldUpdateTimestampOnUpdate() {
        measurement.onCreate();
        LocalDateTime originalUpdatedAt = measurement.getUpdatedAt();
        
        try {
            Thread.sleep(10); // Ensure time difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        measurement.onUpdate();
        
        assertNotEquals(originalUpdatedAt, measurement.getUpdatedAt());
        assertTrue(measurement.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should support Builder pattern")
    void shouldSupportBuilderPattern() {
        LocalDateTime measurementTime = LocalDateTime.of(2024, 3, 1, 10, 0);
        
        Measurement builtMeasurement = Measurement.builder()
                .author("Nurse Johnson")
                .time(measurementTime)
                .weight(5.1)
                .height(58.0)
                .headCircumference(40.5)
                .baby(baby)
                .build();
        
        assertEquals("Nurse Johnson", builtMeasurement.getAuthor());
        assertEquals(measurementTime, builtMeasurement.getTime());
        assertEquals(5.1, builtMeasurement.getWeight());
        assertEquals(58.0, builtMeasurement.getHeight());
        assertEquals(40.5, builtMeasurement.getHeadCircumference());
        assertEquals(baby, builtMeasurement.getBaby());
    }

    @Test
    @DisplayName("Should handle null time")
    void shouldHandleNullTime() {
        measurement.setTime(null);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertNull(measurement.getTime());
    }

    @Test
    @DisplayName("Should handle special characters in author field")
    void shouldHandleSpecialCharactersInAuthorField() {
        measurement.setAuthor("Dr. María José González-Smith, MD, PhD");
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertEquals("Dr. María José González-Smith, MD, PhD", measurement.getAuthor());
    }

    @Test
    @DisplayName("Should handle unicode characters in author field")
    void shouldHandleUnicodeCharactersInAuthorField() {
        measurement.setAuthor("医生 Zhang Wei 张伟");
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertEquals("医生 Zhang Wei 张伟", measurement.getAuthor());
    }

    @Test
    @DisplayName("Should handle long author name")
    void shouldHandleLongAuthorName() {
        String longAuthor = "Dr. " + "A".repeat(200) + " B".repeat(200) + " Smith";
        measurement.setAuthor(longAuthor);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertEquals(longAuthor, measurement.getAuthor());
    }

    @Test
    @DisplayName("Should handle partial measurements")
    void shouldHandlePartialMeasurements() {
        // Test with only weight
        measurement.setHeight(null);
        measurement.setHeadCircumference(null);
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertNotNull(measurement.getWeight());
        assertNull(measurement.getHeight());
        assertNull(measurement.getHeadCircumference());
        
        // Test with only height
        measurement.setWeight(null);
        measurement.setHeight(60.0);
        
        violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertNull(measurement.getWeight());
        assertNotNull(measurement.getHeight());
        assertNull(measurement.getHeadCircumference());
        
        // Test with only head circumference
        measurement.setHeight(null);
        measurement.setHeadCircumference(42.0);
        
        violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        assertNull(measurement.getWeight());
        assertNull(measurement.getHeight());
        assertNotNull(measurement.getHeadCircumference());
    }

    @Test
    @DisplayName("Should handle realistic baby measurement ranges")
    void shouldHandleRealisticBabyMeasurementRanges() {
        // Newborn measurements
        measurement.setWeight(2.5); // kg
        measurement.setHeight(45.0); // cm
        measurement.setHeadCircumference(32.0); // cm
        
        Set<ConstraintViolation<Measurement>> violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        
        // 1-year-old measurements
        measurement.setWeight(10.0); // kg
        measurement.setHeight(75.0); // cm
        measurement.setHeadCircumference(46.0); // cm
        
        violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
        
        // 2-year-old measurements
        measurement.setWeight(12.5); // kg
        measurement.setHeight(85.0); // cm
        measurement.setHeadCircumference(48.0); // cm
        
        violations = validator.validate(measurement);
        assertTrue(violations.isEmpty());
    }
}