package com.example.questionbank.controller;

import com.example.questionbank.entity.Question;
import com.example.questionbank.entity.WrongQuestion;
import com.example.questionbank.entity.User;
import com.example.questionbank.service.QuestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    private boolean notVerified(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Boolean passed = (Boolean) session.getAttribute("vipPassed");

        // 管理员跳过 VIP 验证
        if (user != null && user.isAdmin()) {
            return false;
        }

        return user == null || passed == null || !passed;
    }


    @GetMapping("/upload")
    public String showUploadForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Boolean passed = (Boolean) session.getAttribute("vipPassed");

        if (user == null || passed == null || !passed || !user.isAdmin()) {
            return "redirect:/no-permission";
        }

        model.addAttribute("question", new Question());
        return "upload";
    }
    @GetMapping("/no-permission")
    public String noPermission() {
        return "no-permission";
    }



    @PostMapping("/upload")
    public String uploadQuestion(@ModelAttribute Question question, HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";

        questionService.saveQuestion(question);
        return "redirect:/upload?success";
    }

    @GetMapping("/questions")
    public String showQuestions(Model model, HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";

        model.addAttribute("questions", questionService.getAllQuestions());
        return "questions";
    }

    @GetMapping("/answer")
    public String showAnswerPage(@RequestParam Long id, Model model, HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";

        Question question = questionService.getQuestionById(id);
        model.addAttribute("question", question);
        return "answer";
    }

    @PostMapping("/answer")
    public String submitAnswer(@RequestParam Long id,
                               @RequestParam String userAnswer,
                               Model model,
                               HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";

        Question question = questionService.getQuestionById(id);
        boolean correct = question.getCorrectAnswer().equalsIgnoreCase(userAnswer);

        String result = correct ? "✅ 恭喜，答对了！" : "❌ 很遗憾，答错了。";

        if (!correct) {
            WrongQuestion wrong = new WrongQuestion();
            wrong.setQuestionId(question.getId());
            wrong.setUserAnswer(userAnswer);
            wrong.setCorrectAnswer(question.getCorrectAnswer());
            wrong.setTitle(question.getTitle());
            questionService.saveWrongQuestion(wrong);
        }

        model.addAttribute("question", question);
        model.addAttribute("result", result);
        model.addAttribute("correctAnswer", question.getCorrectAnswer());
        model.addAttribute("explanation", question.getExplanation());

        return "answer";
    }

    @GetMapping("/wrong-questions")
    public String showWrongQuestions(Model model, HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";

        model.addAttribute("wrongs", questionService.getAllWrongQuestions());
        return "wrong";
    }

    @PostMapping("/wrong-questions/clear")
    public String clearWrongQuestions(HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";

        questionService.clearWrongQuestions();
        return "redirect:/wrong-questions";
    }
}
