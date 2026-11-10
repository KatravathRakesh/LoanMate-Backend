package com.loanmanagement.loan_management_system.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "password_reset_otp")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String otpHash;

    private LocalDateTime expiryTime;

    private int attempts;

    private boolean verified;
    private String resetTokenHash;

    private LocalDateTime resetTokenExpiry;
}
