package com.example.questionbank.repository;

import com.example.questionbank.entity.VipCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VipCodeRepository extends JpaRepository<VipCode, Long> {
    VipCode findByCode(String code);
}
