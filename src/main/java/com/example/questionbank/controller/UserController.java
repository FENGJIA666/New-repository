package com.example.questionbank.controller;

import com.example.questionbank.service.QuestionService;
import org.springframework.ui.Model;
import com.example.questionbank.entity.User;
import com.example.questionbank.entity.VipCode;
import com.example.questionbank.repository.UserRepository;
import com.example.questionbank.repository.VipCodeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserRepository userRepository;

    // 显示注册页面
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // 提交注册（VIP 可选）
    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam(required = false) String vipCode,
                             HttpSession session) {
        if (userRepository.findByUsername(username) != null) {
            return "redirect:/register?error";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setVipCode(vipCode); // 可为空

        userRepository.save(user);
        session.setAttribute("user", user);

        return "redirect:/vip-check";
    }


    // 显示登录页面
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 登录验证
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        User user = userRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }

        session.setAttribute("user", user);

        // ✅ 管理员自动通过 VIP 验证
        if (user.isAdmin()) {
            session.setAttribute("vipPassed", true);
        }

        return "redirect:/";
    }


    // 显示 VIP 验证页面
    @GetMapping("/vip-check")
    public String showVipCheckPage() {
        return "vip-check";
    }

    @Autowired
    private VipCodeRepository vipCodeRepository;

    @PostMapping("/vip-check")
    public String doVipCheck(@RequestParam String vipCode, HttpSession session) {
        User user = (User) session.getAttribute("user");
        VipCode code = vipCodeRepository.findByCode(vipCode);

        if (user != null && code != null && !code.isUsed()) {
            session.setAttribute("vipPassed", true);

            // 将该 VIP 码标记为已使用
            code.setUsed(true);
            vipCodeRepository.save(code);

            return "redirect:/questions";
        }

        return "redirect:/vip-check?error";
    }
    @GetMapping("/admin/questions")
    public String showAllQuestionsForAdmin(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/no-permission";
        }

        model.addAttribute("questions", questionService.getAllQuestions());
        return "admin-questions";
    }

    @PostMapping("/admin/questions/delete")
    public String deleteQuestion(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/no-permission";
        }

        questionService.deleteQuestionById(id);
        return "redirect:/admin/questions";
    }



    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
