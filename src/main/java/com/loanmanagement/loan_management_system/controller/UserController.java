
package com.loanmanagement.loan_management_system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.loanmanagement.loan_management_system.entity.User;
import com.loanmanagement.loan_management_system.repository.UserRepository;
import com.loanmanagement.loan_management_system.security.JwtService;
import com.loanmanagement.loan_management_system.service.PasswordResetService;
import com.loanmanagement.loan_management_system.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private JwtService jwtService;

    // Check ADMIN role
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Get currently logged-in user
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName());
    }

    // General safe user details
    // Password, PAN and Aadhaar are not included
    private Map<String, Object> userResponse(User user) {

        Map<String, Object> response = new HashMap<>();

        response.put("uid", user.getUid());
        response.put("uname", user.getUname());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("mobile", user.getMobile());
        response.put("address", user.getAddress());
        response.put("dob", user.getDob());
        response.put("gender", user.getGender());
        response.put("profileImage", user.getProfileImage());

        return response;
    }

    // Profile details: PAN and Aadhaar only for account owner
    private Map<String, Object> profileResponse(User user, boolean isOwner) {

        Map<String, Object> response = userResponse(user);

        if (isOwner) {
            response.put("panNumber", user.getPanNumber());
            response.put("aadhaarNumber", user.getAadhaarNumber());
        }

        return response;
    }

    // 1. Register - Public
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        user.setUid(null);
        user.setRole("CUSTOMER");

        userService.registerUser(user);

        return ResponseEntity.ok(
                Map.of("message", "User registered successfully"));
    }

    // 2. Find User - ADMIN or Account Owner
    @GetMapping("/{uid}")
    public ResponseEntity<?> findUser(
            @PathVariable Long uid,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        if (!isAdmin(authentication) && !currentUser.getUid().equals(uid)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You can only view your own profile"));
        }

        User user = userService.findUser(uid);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isOwner = currentUser.getUid().equals(uid);

        return ResponseEntity.ok(profileResponse(user, isOwner));
    }

    // 3. Update User - ADMIN or Account Owner
    @PutMapping("/{uid}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long uid,
            @RequestBody User user,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        if (!isAdmin(authentication) && !currentUser.getUid().equals(uid)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You can only update your own profile"));
        }

        User updatedUser = userService.updateUser(uid, user);

        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isOwner = currentUser.getUid().equals(uid);

        return ResponseEntity.ok(profileResponse(updatedUser, isOwner));
    }

    // 4. Delete User - ADMIN only
    @DeleteMapping("/{uid}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long uid,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Only Admin can delete users"));
        }

        User targetUser = userService.findUser(uid);

        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        if ("ADMIN".equalsIgnoreCase(targetUser.getRole())) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin accounts cannot be deleted through this API"));
        }

        userService.deleteUser(uid);

        return ResponseEntity.ok(
                Map.of("message", "User deleted successfully"));
    }

    // 5. Login - Public
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> data) {

        String email = data.get("email");
        String password = data.get("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and password are required"));
        }

        User user = userService.loginUser(email, password);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid Email or Password"));
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        Map<String, Object> response = userResponse(user);

        response.put("message", "Login successful");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // 6. Change Password - Account Owner only
    @PostMapping("/{uid}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long uid,
            @RequestBody Map<String, String> data,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        if (!currentUser.getUid().equals(uid)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You can only change your own password"));
        }

        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");

        if (oldPassword == null || newPassword == null
                || oldPassword.isBlank() || newPassword.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Please enter all password fields"));
        }

        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "New password must contain at least 8 characters"));
        }

        boolean changed = userService.changePassword(uid, oldPassword, newPassword);

        if (changed) {
            return ResponseEntity.ok(
                    Map.of("message", "Password changed successfully"));
        }

        return ResponseEntity.badRequest()
                .body(Map.of("message", "Current password is incorrect"));
    }

    // 7. Find All Users - ADMIN only
    @GetMapping("/all")
    public ResponseEntity<?> findAllUsers(Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Only Admin can view all users"));
        }

        List<Map<String, Object>> users = userService.findAllUsers()
                .stream()
                .map(this::userResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // 8. Forgot Password - Public
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> data) {

        String email = data.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email is required"));
        }

        try {
            passwordResetService.sendOtp(email);
        } catch (Exception e) {
            // Keep response generic
        }

        return ResponseEntity.ok(
                Map.of("message", "If the account exists, an OTP will be sent."));
    }

    // 9. Verify OTP - Public
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> data) {

        String email = data.get("email");
        String otp = data.get("otp");

        if (email == null || otp == null || email.isBlank() || otp.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and OTP are required"));
        }

        String resetToken = passwordResetService.verifyOtp(email, otp);

        if (resetToken != null) {
            return ResponseEntity.ok(
                    Map.of("message", "OTP verified successfully", "resetToken", resetToken));
        }

        return ResponseEntity.badRequest()
                .body(Map.of("message", "Invalid or expired OTP"));
    }

    // 10. Reset Password - Public
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> data) {

        String email = data.get("email");
        String resetToken = data.get("resetToken");
        String newPassword = data.get("newPassword");

        if (email == null || resetToken == null || newPassword == null
                || email.isBlank() || resetToken.isBlank()
                || newPassword.length() < 8) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid password reset details"));
        }

        boolean reset = passwordResetService.resetPassword(
                email, resetToken, newPassword);

        if (reset) {
            return ResponseEntity.ok(
                    Map.of("message", "Password reset successfully"));
        }

        return ResponseEntity.badRequest()
                .body(Map.of("message", "Invalid or expired reset token"));
    }
}
