package com.example.questionbank.service;

import com.example.questionbank.entity.WrongQuestion;
import com.example.questionbank.repository.WrongQuestionRepository;
import com.example.questionbank.entity.Question;
import com.example.questionbank.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {



    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private WrongQuestionRepository wrongQuestionRepository;

    public List<WrongQuestion> getAllWrongQuestions() {
        return wrongQuestionRepository.findAll();
    }


    public void saveWrongQuestion(WrongQuestion wrong) {
        wrongQuestionRepository.save(wrong);
    }


    // 保存题目
    public void saveQuestion(Question question) {
        questionRepository.save(question);
    }

    public void clearWrongQuestions() {
        wrongQuestionRepository.deleteAll();
    }

    // 查询全部题目
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // 按 ID 查找题目（用于后面展示解析）
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }
    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
    }

}
