package com.babypal.models;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.babypal.converters.StringListConverter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "growth_guide")
public class GrowthGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "month_range")
    private String monthRange;

    @Column(name = "age_description")
    private String ageDescription;

    @Column(name = "physical_development")
    @Convert(converter = StringListConverter.class)
    private List<String> physicalDevelopment;

    @Column(name = "cognitive_social")
    @Convert(converter = StringListConverter.class)
    private List<String> cognitiveSocial;

    @Column(name = "motor_skills")
    @Convert(converter = StringListConverter.class)
    private List<String> motorSkills;

}
