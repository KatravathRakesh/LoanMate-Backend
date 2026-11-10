package com.loanmanagement.loan_management_system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanmanagement.loan_management_system.entity.Loan;
import com.loanmanagement.loan_management_system.repository.UserRepository;
import com.loanmanagement.loan_management_system.service.LoanService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/loans")
public class LoanController {

	@Autowired
	private LoanService loanService;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/addLoan")
	public ResponseEntity<?> addLoan(@RequestBody Loan loan, Authentication authentication) {

		boolean isCustomer = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

		if (!isCustomer) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Customers can create loans"));
		}

		var user = userRepository.findByEmail(authentication.getName());

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		// Always take the customer ID from the authenticated user
		loan.setUserId(user.getUid());

		// Do not accept a loan ID from the frontend
		loan.setLoanId(null);

		loanService.addLoan(loan);

		return ResponseEntity.ok(Map.of("message", "Loan added successfully"));
	}

	@GetMapping("/getAllLoan")
	public List<Loan> findAllLoan() {
		return loanService.getAllLoans();
	}

	@GetMapping("/getLoan/{loanId}")
	public ResponseEntity<?> findLoanById(@PathVariable Long loanId, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (isAdmin) {
			return ResponseEntity.ok(loanService.getLoanById(loanId));
		}

		var user = userRepository.findByEmail(authentication.getName());

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		Loan loan = loanService.getLoanByIdAndUserId(loanId, user.getUid());

		if (loan == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(loan);
	}

	@GetMapping("/GetLoanByStatus/{status}")
	public ResponseEntity<?> getLoansByUserId(@PathVariable String status, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Admin can access this API"));
		}

		return ResponseEntity.ok(loanService.getLoanByStatus(status));
	}

	@GetMapping("/GetLoanByUserId/{userId}")
	public ResponseEntity<?> getLoansByUserId(@PathVariable Long userId, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (isAdmin) {
			return ResponseEntity.ok(loanService.getLoanByUserId(userId));
		}

		var user = userRepository.findByEmail(authentication.getName());

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		if (!user.getUid().equals(userId)) {
			return ResponseEntity.status(403).body(Map.of("message", "You cannot access another customer's loans"));
		}

		return ResponseEntity.ok(loanService.getLoanByUserId(user.getUid()));
	}

	@GetMapping("/count")
	public ResponseEntity<?> countLoans(Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Admin can access this API"));
		}

		return ResponseEntity.ok(loanService.countLoans());
	}

	@GetMapping("/count/active")
	public ResponseEntity<?> countActiveLoans(Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Admin can access this API"));
		}

		return ResponseEntity.ok(loanService.countActiveLoans());
	}

	@PutMapping("/updatebyId/{loanId}/status")
	public ResponseEntity<?> updateLoanStatus(@PathVariable Long loanId, @RequestBody String status,
			Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Admin can update loan status"));
		}

		Loan updatedLoan = loanService.updateLoanStatus(loanId, status);

		return ResponseEntity.ok(Map.of("message", "Loan status updated successfully", "loanId",
				updatedLoan.getLoanId(), "status", updatedLoan.getStatus()));
	}

	@DeleteMapping("/deleteLoanById/{loanId}")
	public ResponseEntity<?> deleteLoanByid(@PathVariable Long loanId, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Admin can delete loans"));
		}

		loanService.deleteLoan(loanId);

		return ResponseEntity.ok(Map.of("message", "Loan deleted successfully"));
	}

	@GetMapping("/{loanId}/summary")
	public ResponseEntity<?> getLoanSummary(@PathVariable Long loanId, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

		Loan loan;

		if (isAdmin) {
			loan = loanService.getLoanById(loanId);
		} else {
			var user = userRepository.findByEmail(authentication.getName());

			if (user == null) {
				return ResponseEntity.status(401).build();
			}

			loan = loanService.getLoanByIdAndUserId(loanId, user.getUid());

			if (loan == null) {
				return ResponseEntity.notFound().build();
			}
		}

		Map<String, Object> summary = new HashMap<>();

		summary.put("loanId", loan.getLoanId());
		summary.put("loanAmount", loan.getLoanAmount());
		summary.put("totalPaid", loan.getTotalPaid());
		summary.put("remainingAmount", loan.getRemainingAmount());
		summary.put("status", loan.getStatus());

		return ResponseEntity.ok(summary);
	}

	@GetMapping("/user/{userId}/active")
	public ResponseEntity<?> getActiveLoansByUserId(@PathVariable Long userId, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		var user = userRepository.findByEmail(authentication.getName());

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		if (!isAdmin && !user.getUid().equals(userId)) {
			return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
		}

		return ResponseEntity.ok(loanService.getLoanByUserId(userId).stream()
				.filter(loan -> "ACTIVE".equalsIgnoreCase(loan.getStatus())).toList());
	}

	@GetMapping("/user/{userId}/active/total")
	public ResponseEntity<?> getTotalActiveLoanAmount(@PathVariable Long userId, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		var user = userRepository.findByEmail(authentication.getName());

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		if (!isAdmin && !user.getUid().equals(userId)) {
			return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
		}

		double total = loanService.getLoanByUserId(userId).stream()
				.filter(loan -> "ACTIVE".equalsIgnoreCase(loan.getStatus()))
				.mapToDouble(loan -> loan.getLoanAmount() == null ? 0.0 : loan.getLoanAmount()).sum();

		return ResponseEntity.ok(total);
	}

	@GetMapping("/user/{userId}/active/remaining")
	public ResponseEntity<?> getTotalRemainingAmount(@PathVariable Long userId, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		var user = userRepository.findByEmail(authentication.getName());

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		if (!isAdmin && !user.getUid().equals(userId)) {
			return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
		}

		double total = loanService.getLoanByUserId(userId).stream()
				.filter(loan -> "ACTIVE".equalsIgnoreCase(loan.getStatus()))
				.mapToDouble(loan -> loan.getRemainingAmount() == null ? 0.0 : loan.getRemainingAmount()).sum();

		return ResponseEntity.ok(total);
	}

}
