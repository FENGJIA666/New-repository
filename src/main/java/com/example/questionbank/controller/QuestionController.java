package com.example.questionbank.controller;

import com.example.questionbank.entity.Question;
import com.example.questionbank.entity.User;
import com.example.questionbank.entity.WrongQuestion;
import com.example.questionbank.service.QuestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    private boolean notVerified(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Boolean passed = (Boolean) session.getAttribute("vipPassed");
        return user == null || passed == null || (!passed && (user == null || !user.isAdmin()));
    }

    // 显示上传页面
    @GetMapping("/upload")
    public String showUploadForm(Model model, HttpSession session) {
        if (notVerified(session) || !((User) session.getAttribute("user")).isAdmin()) {
            return "redirect:/no-permission";
        }
        model.addAttribute("question", new Question());
        return "upload";
    }

    // 上传 PDF 文件
    @PostMapping("/uploadPdf")
    public String uploadPdf(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        if (notVerified(session) || !((User) session.getAttribute("user")).isAdmin()) {
            return "redirect:/no-permission";
        }

        if (file.isEmpty()) {
            model.addAttribute("message", "文件为空！");
            return "upload";
        }

        String uploadDir = new File("uploads").getAbsolutePath();
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, fileName);

        try {
            file.transferTo(dest);
            model.addAttribute("pdfUrl", "/uploads/" + fileName);
            model.addAttribute("message", "上传成功！");
        } catch (IOException e) {
            model.addAttribute("message", "上传失败：" + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("question", new Question());
        return "upload";
    }

    // 上传题目表单提交
    @PostMapping("/upload")
    public String uploadQuestion(@ModelAttribute Question question, HttpSession session) {
        if (notVerified(session) || !((User) session.getAttribute("user")).isAdmin()) {
            return "redirect:/no-permission";
        }

        questionService.saveQuestion(question);
        return "redirect:/upload?success";
    }

    // 做题模式：开始
    @GetMapping("/start")
    public String startQuestion(HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";
        Question random = questionService.getRandomQuestion();
        if (random == null) return "redirect:/questions";
        session.setAttribute("currentQuestion", random);
        return "redirect:/do-question";
    }

    // 做题模式：展示页面
    @GetMapping("/do-question")
    public String doQuestion(Model model, HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";

        Question q = (Question) session.getAttribute("currentQuestion");
        if (q == null) return "redirect:/start";

        model.addAttribute("question", q);
        return "doquestion";
    }

    // 做题模式：提交答案
    @PostMapping("/submit-answer")
    public String submitDoAnswer(@RequestParam String userAnswer,
                                 Model model,
                                 HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";

        Question question = (Question) session.getAttribute("currentQuestion");
        if (question == null) return "redirect:/start";

        boolean correct = question.getCorrectAnswer().equalsIgnoreCase(userAnswer);

        if (!correct) {
            WrongQuestion wrong = new WrongQuestion();
            wrong.setQuestionId(question.getId());
            wrong.setUserAnswer(userAnswer);
            wrong.setCorrectAnswer(question.getCorrectAnswer());
            wrong.setTitle(question.getTitle());
            questionService.saveWrongQuestion(wrong);
        }

        model.addAttribute("question", question);
        model.addAttribute("result", correct ? "✅ 正确" : "❌ 错误");
        model.addAttribute("explanation", question.getExplanation());

        return "doquestion";
    }

    // 查看题库
    @GetMapping("/questions")
    public String showQuestions(Model model, HttpSession session) {
        if (notVerified(session)) return "redirect:/vip-check";
        model.addAttribute("questions", questionService.getAllQuestions());
        return "questions";
    }

    // 错题本
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

    @GetMapping("/no-permission")
    public String noPermission() {
        return "no-permission";
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

        if (!correct) {
            WrongQuestion wrong = new WrongQuestion();
            wrong.setQuestionId(question.getId());
            wrong.setUserAnswer(userAnswer);
            wrong.setCorrectAnswer(question.getCorrectAnswer());
            wrong.setTitle(question.getTitle());
            questionService.saveWrongQuestion(wrong);
        }

        model.addAttribute("question", question);
        model.addAttribute("result", correct ? "✅ 正确" : "❌ 错误");
        model.addAttribute("explanation", question.getExplanation());

        return "answer";
    }
}
