package com.example.questionbank.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vip_codes")
public class VipCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private boolean used = false;

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
