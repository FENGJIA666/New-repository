package com.example.questionbank.controller;

import com.example.questionbank.entity.User;
import com.example.questionbank.entity.VipCode;
import com.example.questionbank.repository.VipCodeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/vip")
public class VipAdminController {

    @Autowired
    private VipCodeRepository vipCodeRepository;

    @GetMapping
    public String showVipCodes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) return "redirect:/no-permission";

        model.addAttribute("codes", vipCodeRepository.findAll());
        return "vip-admin";
    }

    @PostMapping("/add")
    public String addVipCode(@RequestParam String newCode, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) return "redirect:/no-permission";

        // 检查是否重复
        if (vipCodeRepository.findByCode(newCode) != null) {
            return "redirect:/admin/vip?duplicate";
        }

        VipCode code = new VipCode();
        code.setCode(newCode);
        code.setUsed(false);
        vipCodeRepository.save(code);

        return "redirect:/admin/vip?added";
    }
}
