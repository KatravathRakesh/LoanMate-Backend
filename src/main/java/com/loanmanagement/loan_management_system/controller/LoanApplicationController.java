
package com.loanmanagement.loan_management_system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

import com.loanmanagement.loan_management_system.entity.LoanApplication;
import com.loanmanagement.loan_management_system.entity.User;
import com.loanmanagement.loan_management_system.repository.UserRepository;
import com.loanmanagement.loan_management_system.service.LoanApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/loan")
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanService;

    @Autowired
    private UserRepository userRepository;

    // Check ADMIN role
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Check ADMIN or BACKUP_ADMIN role
    private boolean isAdminOrBackupAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN") ||
                        a.getAuthority().equals("ROLE_BACKUP_ADMIN"));
    }

    // Get logged-in user from JWT email
    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName());
    }

    // 1. Apply Loan - CUSTOMER only
    @PostMapping("/apply")
    public ResponseEntity<?> applyLoan(
            @Valid @RequestBody LoanApplication loan,
            Authentication authentication) {

        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Only Customers can apply for loans"));
        }

        User user = getCurrentUser(authentication);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "User not found"));
        }

        // Never trust userId or applicationId from frontend
        loan.setApplicationId(null);
        loan.setUserId(user.getUid());
        loan.setStatus("PENDING");

        loanService.applyLoan(loan);

        return ResponseEntity.ok(
                Map.of("message", "Loan application submitted successfully"));
    }

    // 2. Find All Applications - ADMIN or BACKUP_ADMIN
    @GetMapping("/findAllLoan")
    public ResponseEntity<?> findAllLoan(Authentication authentication) {

        if (!isAdminOrBackupAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Access denied"));
        }

        return ResponseEntity.ok(loanService.findAllLoan());
    }

    // 3. Find Application By ID - ADMIN or BACKUP_ADMIN
    @GetMapping("/findById/{applicationId}")
    public ResponseEntity<?> findLoanById(
            @PathVariable Long applicationId,
            Authentication authentication) {

        if (!isAdminOrBackupAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Access denied"));
        }

        LoanApplication loan = loanService.findLoanById(applicationId);

        if (loan == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(loan);
    }

    // 4. Update Application Status - ADMIN only
    @PutMapping("/updateById/{applicationId}")
    public ResponseEntity<?> updateLoanStatus(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Only Admin can update application status"));
        }

        String status = request.get("status");

        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Status is required"));
        }

        LoanApplication updatedLoan =
                loanService.updateLoanStatus(applicationId, status);

        return ResponseEntity.ok(updatedLoan);
    }

    // 5. Delete Application - ADMIN only
    @DeleteMapping("/deleteById/{applicationId}")
    public ResponseEntity<?> deleteLoanById(
            @PathVariable Long applicationId,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Only Admin can delete applications"));
        }

        loanService.deleteLoan(applicationId);

        return ResponseEntity.ok(
                Map.of("message", "Loan application deleted successfully"));
    }

    // 6. Find Applications By User ID - ADMIN, BACKUP_ADMIN or Account Owner
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findLoanByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        if (!isAdminOrBackupAdmin(authentication) &&
                !user.getUid().equals(userId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "You can only view your own applications"));
        }

        return ResponseEntity.ok(loanService.findLoansByUserId(userId));
    }

    // 7. Find Applications By Status - ADMIN or BACKUP_ADMIN
    @GetMapping("/loan/status/{status}")
    public ResponseEntity<?> findLoanByStatus(
            @PathVariable String status,
            Authentication authentication) {

        if (!isAdminOrBackupAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Access denied"));
        }

        return ResponseEntity.ok(loanService.findLoanByStatus(status));
    }

    // 8. Count Pending Applications - ADMIN or BACKUP_ADMIN
    @GetMapping("/count/pending")
    public ResponseEntity<?> countPendingApplications(Authentication authentication) {

        if (!isAdminOrBackupAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(loanService.countPendingApplications());
    }

    // 9. Count Approved Applications - ADMIN or BACKUP_ADMIN
    @GetMapping("/count/Approved")
    public ResponseEntity<?> countApprovedApplication(Authentication authentication) {

        if (!isAdminOrBackupAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(loanService.countApprovedApplications());
    }

    // 10. Count Rejected Applications - ADMIN or BACKUP_ADMIN
    @GetMapping("/count/Rejected")
    public ResponseEntity<?> countRejectedApplication(Authentication authentication) {

        if (!isAdminOrBackupAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(loanService.countRejectedApplications());
    }

    // 11. Count All Applications - ADMIN or BACKUP_ADMIN
    @GetMapping("/countAll")
    public ResponseEntity<?> countAllApplication(Authentication authentication) {

        if (!isAdminOrBackupAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(loanService.countAllApplications());
    }

    // 12. Find Approved Applications - ADMIN or BACKUP_ADMIN
    @GetMapping("/status/approved")
    public ResponseEntity<?> getApprovedApplications(Authentication authentication) {

        if (!isAdminOrBackupAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                loanService.findLoanByStatus("APPROVED"));
    }
}
