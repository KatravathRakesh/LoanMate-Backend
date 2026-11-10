package com.loanmanagement.loan_management_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.loanmanagement.loan_management_system.entity.User;
import com.loanmanagement.loan_management_system.repository.UserRepository;

@Configuration
public class AdminInitializer {

	@Value("${ADMIN_NAME:Admin}")
	private String adminName;

	@Value("${ADMIN_EMAIL:admin@example.com}")
	private String adminEmail;

	@Value("${ADMIN_PASSWORD:}")
	private String adminPassword;

	@Bean
	CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {

		return _ -> {

			if (adminPassword.isBlank()) {
			    System.out.println("Admin creation skipped: ADMIN_PASSWORD is not configured.");
			    return;
			}
			long adminCount = userRepository.countByRole("ADMIN");

			if (adminCount > 1) {
			    throw new IllegalStateException(
			        "Multiple Admin accounts found. Manual review required."
			    );
			}

			if (adminCount == 1) {
			    System.out.println("Admin account already exists.");
			    return;
			}

			if (userRepository.existsByEmail(adminEmail)) {
			    throw new IllegalStateException(
			        "Configured Admin email already belongs to another account."
			    );
			}

			User admin = new User();

			admin.setUname(adminName);
			admin.setEmail(adminEmail);
			admin.setPassword(passwordEncoder.encode(adminPassword));
			admin.setRole("ADMIN");

			userRepository.save(admin);

			System.out.println("Predefined Admin created successfully.");
		};
	}
}
