package com.example.questionbank.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // ✅ 改为非保留字
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String vipCode;

    // 添加字段：是否管理员
    @Column(name = "is_admin")
    private boolean isAdmin = false;

    // Getter
    public boolean isAdmin() {
        return isAdmin;
    }

    // Setter
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }


    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVipCode() {
        return vipCode;
    }

    public void setVipCode(String vipCode) {
        this.vipCode = vipCode;
    }
}
