package com.loanmanagement.loan_management_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.loanmanagement.loan_management_system.entity.User;
import com.loanmanagement.loan_management_system.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public String registerUser(@RequestBody User user) {
	    userService.registerUser(user);
	    return "User Registered Successfully";
	}

	@GetMapping("/{uid}")
	public User findUser(@PathVariable Long uid) {
		return userService.findUser(uid);
	}

	@PutMapping("/{uid}")
	public ResponseEntity<?> updateUser(@PathVariable Long uid, @RequestBody User user) {

		User updatedUser = userService.updateUser(uid, user);

		if (updatedUser == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{uid}")
	public String deleteUser(@PathVariable Long uid) {
		userService.deleteUser(uid);

		return "User Deleted Successfully";
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody Map<String, String> data) {

		String email = data.get("email");
		String password = data.get("password");

		User user = userService.loginUser(email, password);

		if (user != null) {

			Map<String, Object> response = new HashMap<>();

			response.put("message", "Login Successful");
			response.put("uid", user.getUid());
			response.put("uname", user.getUname());
			response.put("email", user.getEmail());
			response.put("role", user.getRole());
			response.put("mobile", user.getMobile());
			response.put("address", user.getAddress());

			return ResponseEntity.ok(response);
		}

		return ResponseEntity.status(401).body(Map.of("message", "Invalid Email or Password"));
	}

	@PostMapping("/{uid}/change-password")
	public ResponseEntity<?> changePassword(@PathVariable Long uid, @RequestBody Map<String, String> data) {

		String oldPassword = data.get("oldPassword");
		String newPassword = data.get("newPassword");

		if (oldPassword == null || newPassword == null || oldPassword.isBlank() || newPassword.isBlank()) {
			return ResponseEntity.badRequest().body(Map.of("message", "Please enter all password fields"));
		}

		if (newPassword.length() < 8) {
			return ResponseEntity.badRequest()
					.body(Map.of("message", "New password must contain at least 8 characters"));
		}

		boolean changed = userService.changePassword(uid, oldPassword, newPassword);

		if (changed) {
			return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
		}

		return ResponseEntity.badRequest().body(Map.of("message", "Current password is incorrect"));
	}
	
	@GetMapping("/all")
	public List<User> findAllUsers() {
	    return userService.findAllUsers();
	}

}
