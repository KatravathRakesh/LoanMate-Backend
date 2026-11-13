package com.loanmanagement.loan_management_system.service;

import java.util.List;

import com.loanmanagement.loan_management_system.entity.User;

public interface UserService {

	// Register a new customer
	void registerUser(User u);
	
	// Register a new Backup Admin
	void registerBackupAdmin(User user);

	// Find a customer
	User findUser(Long uid);

	// Update a customer
	User updateUser(Long uid, User u);

	// Delete a customer
	void deleteUser(Long uid);

	// Login customer
	User loginUser(String email, String password);

//    change password
	boolean changePassword(Long uid, String oldPassword, String newPassword);
	
	List<User> findAllUsers();

}
