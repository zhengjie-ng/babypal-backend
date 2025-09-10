package com.babypal.converters;

import java.util.List;
import java.util.Arrays;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting list to JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return List.of();
        }
        try {
            return Arrays.asList(objectMapper.readValue(dbData, String[].class));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to list", e);
        }
    }
}