package com.loanmanagement.loan_management_system.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("LoanMate - Password Reset OTP");

        message.setText(
            "Hello,\n\n" +
            "Your LoanMate password reset OTP is: " + otp + "\n\n" +
            "This OTP is valid for 5 minutes.\n\n" +
            "If you did not request this, please ignore this email.\n\n" +
            "Regards,\nLoanMate Team"
        );

        mailSender.send(message);
    }
}

