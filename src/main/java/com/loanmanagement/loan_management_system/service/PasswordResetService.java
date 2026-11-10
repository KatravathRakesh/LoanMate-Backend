
package com.loanmanagement.loan_management_system.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.loanmanagement.loan_management_system.entity.PasswordResetOtp;
import com.loanmanagement.loan_management_system.repository.PasswordResetOtpRepository;
import com.loanmanagement.loan_management_system.repository.UserRepository;

@Service
public class PasswordResetService {

	@Autowired
	private PasswordResetOtpRepository otpRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final SecureRandom random = new SecureRandom();

	public void sendOtp(String email) {

		if (userRepository.findByEmail(email) == null) {
			throw new RuntimeException("If the account exists, an OTP will be sent.");
		}

		otpRepository.deleteByEmail(email);

		String otp = String.format("%06d", random.nextInt(1000000));

		PasswordResetOtp resetOtp = new PasswordResetOtp();
		resetOtp.setEmail(email);
		resetOtp.setOtpHash(passwordEncoder.encode(otp));
		resetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
		resetOtp.setAttempts(0);
		resetOtp.setVerified(false);

		otpRepository.save(resetOtp);

		emailService.sendOtpEmail(email, otp);
	}

	public String verifyOtp(String email, String otp) {

		PasswordResetOtp resetOtp = otpRepository.findByEmail(email).orElse(null);

		if (resetOtp == null || resetOtp.isVerified()) {
			return null;
		}

		if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
			otpRepository.delete(resetOtp);
			return null;
		}

		if (resetOtp.getAttempts() >= 5) {
			otpRepository.delete(resetOtp);
			return null;
		}

		resetOtp.setAttempts(resetOtp.getAttempts() + 1);

		if (!passwordEncoder.matches(otp, resetOtp.getOtpHash())) {
			otpRepository.save(resetOtp);
			return null;
		}

		String resetToken = UUID.randomUUID().toString();

		resetOtp.setVerified(true);
		resetOtp.setResetTokenHash(passwordEncoder.encode(resetToken));
		resetOtp.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));

		otpRepository.save(resetOtp);

		return resetToken;
	}

	public boolean resetPassword(String email, String resetToken, String newPassword) {

		PasswordResetOtp resetOtp = otpRepository.findByEmail(email).orElse(null);

		if (resetOtp == null || !resetOtp.isVerified()) {
			return false;
		}

		if (resetOtp.getResetTokenExpiry() == null || resetOtp.getResetTokenExpiry().isBefore(LocalDateTime.now())) {

			otpRepository.delete(resetOtp);
			return false;
		}

		if (resetOtp.getResetTokenHash() == null
				|| !passwordEncoder.matches(resetToken, resetOtp.getResetTokenHash())) {
			return false;
		}

		var user = userRepository.findByEmail(email);

		if (user == null) {
			return false;
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		userRepository.save(user);

		// Delete the token after successful password reset
		otpRepository.delete(resetOtp);

		return true;
	}

}
