
package com.loanmanagement.loan_management_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.loanmanagement.loan_management_system.entity.User;
import com.loanmanagement.loan_management_system.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 1. Register User
	@Override
	public void registerUser(User user) {

		if (user.getEmail() == null || user.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}

		if (user.getPassword() == null || user.getPassword().length() < 8) {
			throw new IllegalArgumentException("Password must contain at least 8 characters");
		}

		if (userRepo.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("Email already registered");
		}

		// Backend controls ID, role and password
		user.setUid(null);
		user.setRole("CUSTOMER");
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepo.save(user);
	}
	
	// Register Backup Admin
	@Override
	public void registerBackupAdmin(User user) {

	    if (user.getEmail() == null || user.getEmail().isBlank()) {
	        throw new IllegalArgumentException("Email is required");
	    }

	    if (user.getPassword() == null || user.getPassword().length() < 8) {
	        throw new IllegalArgumentException("Password must contain at least 8 characters");
	    }

	    if (userRepo.existsByEmail(user.getEmail())) {
	        throw new IllegalArgumentException("Email already registered");
	    }

	    // Backend controls ID, role and password
	    user.setUid(null);
	    user.setRole("BACKUP_ADMIN");
	    user.setPassword(passwordEncoder.encode(user.getPassword()));

	    userRepo.save(user);
	}

	// 2. Find User By ID
	@Override
	public User findUser(Long uid) {
		return userRepo.findById(uid).orElse(null);
	}

	// 3. Update User
	@Override
	public User updateUser(Long uid, User updatedData) {

		User user = userRepo.findById(uid).orElse(null);

		if (user == null) {
			return null;
		}

		if (updatedData.getUname() != null && !updatedData.getUname().isBlank()) {
			user.setUname(updatedData.getUname());
		}

		if (updatedData.getEmail() != null && !updatedData.getEmail().isBlank()) {

			User existingUser = userRepo.findByEmail(updatedData.getEmail());

			if (existingUser != null && !existingUser.getUid().equals(uid)) {
				throw new IllegalArgumentException("Email already registered");
			}

			user.setEmail(updatedData.getEmail());
		}

		if (updatedData.getMobile() != null && !updatedData.getMobile().isBlank()) {
			user.setMobile(updatedData.getMobile());
		}

		if (updatedData.getAddress() != null && !updatedData.getAddress().isBlank()) {
			user.setAddress(updatedData.getAddress());
		}

		if (updatedData.getDob() != null) {
			user.setDob(updatedData.getDob());
		}

		if (updatedData.getGender() != null && !updatedData.getGender().isBlank()) {
			user.setGender(updatedData.getGender());
		}

		if (updatedData.getPanNumber() != null && !updatedData.getPanNumber().isBlank()) {
			user.setPanNumber(updatedData.getPanNumber());
		}

		if (updatedData.getAadhaarNumber() != null && !updatedData.getAadhaarNumber().isBlank()) {
			user.setAadhaarNumber(updatedData.getAadhaarNumber());
		}

		if (updatedData.getProfileImage() != null && !updatedData.getProfileImage().isBlank()) {
			user.setProfileImage(updatedData.getProfileImage());
		}

		// Never accept role or password changes through this method
		return userRepo.save(user);
	}

	// 4. Delete User
	@Override
	public void deleteUser(Long uid) {

		if (!userRepo.existsById(uid)) {
			throw new RuntimeException("User not found");
		}

		userRepo.deleteById(uid);
	}

	// 5. Login User
	@Override
	public User loginUser(String email, String password) {

		User user = userRepo.findByEmail(email);

		if (user != null && passwordEncoder.matches(password, user.getPassword())) {
			return user;
		}

		return null;
	}

	// 6. Change Password
	@Override
	public boolean changePassword(Long uid, String oldPassword, String newPassword) {

		User user = userRepo.findById(uid).orElse(null);

		if (user == null) {
			return false;
		}

		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			return false;
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		userRepo.save(user);

		return true;
	}

	// 7. Find All Users
	@Override
	public List<User> findAllUsers() {
		return userRepo.findAll();
	}
}
