package com.loanmanagement.loan_management_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loanmanagement.loan_management_system.entity.User;
import com.loanmanagement.loan_management_system.repository.UserRepository;

@Service
public class UserServiceImp implements UserService {

	@Autowired
	UserRepository userRepo;

	@Override
	public void registerUser(User u) {
		userRepo.save(u);

	}

	@Override
	public User findUser(Long uid) {
		return userRepo.findById(uid).orElse(null);

	}

	@Override
	public User updateUser(Long uid, User u) {

		User user = userRepo.findById(uid).orElse(null);

		if (user == null) {
			return null;
		}

		// Update Full Name
		if (u.getUname() != null && !u.getUname().isEmpty()) {
			user.setUname(u.getUname());
		}

		// Update Email
		if (u.getEmail() != null && !u.getEmail().isEmpty()) {
			user.setEmail(u.getEmail());
		}

		// Update Mobile Number
		if (u.getMobile() != null && !u.getMobile().isEmpty()) {
			user.setMobile(u.getMobile());
		}

		// Update Address
		if (u.getAddress() != null && !u.getAddress().isEmpty()) {
			user.setAddress(u.getAddress());
		}

		// Update Date of Birth
		if (u.getDob() != null) {
			user.setDob(u.getDob());
		}

		// Update Gender
		if (u.getGender() != null && !u.getGender().isEmpty()) {
			user.setGender(u.getGender());
		}

		// Update PAN Number
		if (u.getPanNumber() != null && !u.getPanNumber().isEmpty()) {
			user.setPanNumber(u.getPanNumber());
		}

		// Update Aadhaar Number
		if (u.getAadhaarNumber() != null && !u.getAadhaarNumber().isEmpty()) {
			user.setAadhaarNumber(u.getAadhaarNumber());
		}

		// Update profile image
		if (u.getProfileImage() != null && !u.getProfileImage().isEmpty()) {
		    user.setProfileImage(u.getProfileImage());
		}
		
		return userRepo.save(user);
	}

	@Override
	public void deleteUser(Long uid) {
		userRepo.deleteById(uid);

	}

	@Override
	public User loginUser(String email, String password) {
		User user = userRepo.findByEmail(email);

		if (user != null && user.getPassword().equals(password)) {
			return user;
		}

		return null;

	}

	@Override
	public boolean changePassword(Long uid, String oldPassword, String newPassword) {
		User user = userRepo.findById(uid).orElse(null);

		if (user == null) {
			return false;
		}

		// Check current password
		if (!user.getPassword().equals(oldPassword)) {
			return false;
		}

		// Update new password
		user.setPassword(newPassword);

		userRepo.save(user);

		return true;
	}

	@Override
	public List<User> findAllUsers() {
	    return userRepo.findAll();
	}
}
