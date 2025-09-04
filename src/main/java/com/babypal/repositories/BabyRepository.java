package com.babypal.repositories;

import com.babypal.models.Baby;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BabyRepository extends JpaRepository<Baby, Long>{

}
