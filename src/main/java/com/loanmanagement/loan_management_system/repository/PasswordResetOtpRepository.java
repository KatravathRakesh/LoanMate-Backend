package com.loanmanagement.loan_management_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loanmanagement.loan_management_system.entity.PasswordResetOtp;

@Repository
public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmail(String email);

    void deleteByEmail(String email);
}
