package com.example.questionbank.repository;

import com.example.questionbank.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 不需要自己写方法，Spring 会自动生成
}
