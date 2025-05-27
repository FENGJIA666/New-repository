package com.example.questionbank;

import com.example.questionbank.entity.User;
import com.example.questionbank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuestionbankApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuestionbankApplication.class, args);
    }

    // 👇 启动后自动创建一个管理员账号（用户名 admin / 密码 123456）
    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("123456"); // 注意：此处未加密，仅用于测试
                admin.setIsAdmin(true);
                userRepository.save(admin);
                System.out.println("✅ 管理员账号 admin 已自动创建，密码 123456");
            }
        };
    }
}
