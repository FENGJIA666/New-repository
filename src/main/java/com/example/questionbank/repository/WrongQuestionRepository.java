package com.example.questionbank.repository;

import com.example.questionbank.entity.WrongQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WrongQuestionRepository extends JpaRepository<WrongQuestion, Long> {
}
