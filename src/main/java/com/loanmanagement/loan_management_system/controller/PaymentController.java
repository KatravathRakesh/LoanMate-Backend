package com.loanmanagement.loan_management_system.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanmanagement.loan_management_system.entity.Payment;
import com.loanmanagement.loan_management_system.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.loanmanagement.loan_management_system.entity.User;
import com.loanmanagement.loan_management_system.repository.UserRepository;
import java.time.LocalDate;
import java.util.UUID;
import com.loanmanagement.loan_management_system.repository.LoanRepository;


@RestController
@RequestMapping("/payments")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LoanRepository loanRepository;

	@PostMapping("/addPayments")
	public ResponseEntity<?> addPayment(@RequestBody Payment payment, Authentication authentication) {

		boolean isCustomer = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

		if (!isCustomer) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Customers can initiate payments"));
		}

		User user = userRepository.findByEmail(authentication.getName());

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		// Set trusted values on the backend
		payment.setPaymentId(null);
		payment.setUserId(user.getUid());
		payment.setPaymentStatus("PENDING");
		payment.setPaymentDate(LocalDate.now().toString());
		payment.setTransactionReference(UUID.randomUUID().toString());

		paymentService.addPayment(payment);

		return ResponseEntity.ok(Map.of("message", "Payment submitted successfully", "status", "PENDING"));
	}

	@GetMapping("/GetAllPayments")
	public ResponseEntity<?> getAllPayments(Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		if (!isAdmin) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Admin can view all payments"));
		}

		return ResponseEntity.ok(paymentService.getAllPayments());
	}

	@GetMapping("/findPayById/{paymentId}")
	public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId, Authentication authentication) {

		boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		Payment payment = paymentService.getPaymentById(paymentId);

		if (isAdmin) {
			return ResponseEntity.ok(payment);
		}

		User user = userRepository.findByEmail(authentication.getName());

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		if (!payment.getUserId().equals(user.getUid())) {
			return ResponseEntity.status(404).body(Map.of("message", "Payment not found"));
		}

		return ResponseEntity.ok(payment);
	}

	@GetMapping("/userID/{userId}")
	public ResponseEntity<?> getPaymentsByUserId(@PathVariable Long userId, Authentication authentication) {

		if (isAdmin(authentication)) {
			return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
		}

		User user = getCurrentUser(authentication);

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		if (!user.getUid().equals(userId)) {
			return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
		}

		return ResponseEntity.ok(paymentService.getPaymentsByUserId(user.getUid()));
	}

	@GetMapping("/loanID/{loanId}")
	public ResponseEntity<?> getPaymentsByLoanId(@PathVariable Long loanId, Authentication authentication) {

		var loan = loanRepository.findById(loanId).orElse(null);

		if (loan == null) {
			return ResponseEntity.notFound().build();
		}

		if (isAdmin(authentication)) {
			return ResponseEntity.ok(paymentService.getPaymentsByLoanId(loanId));
		}

		User user = getCurrentUser(authentication);

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		if (!loan.getUserId().equals(user.getUid())) {
			return ResponseEntity.status(404).body(Map.of("message", "Loan not found"));
		}

		return ResponseEntity.ok(paymentService.getPaymentsByLoanId(loanId));
	}

	@GetMapping("/status/{paymentStatus}")
	public ResponseEntity<?> getPaymentsByStatus(@PathVariable String paymentStatus, Authentication authentication) {

		if (!isAdmin(authentication)) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Admin can access this API"));
		}

		return ResponseEntity.ok(paymentService.getPaymentsByStatus(paymentStatus));
	}

	@GetMapping("/count/status/{paymentStatus}")
	public ResponseEntity<?> countPaymentsByStatus(@PathVariable String paymentStatus, Authentication authentication) {

		if (!isAdmin(authentication)) {
			return ResponseEntity.status(403).body(Map.of("message", "Only Admin can access this API"));
		}

		return ResponseEntity.ok(paymentService.countPaymentsByStatus(paymentStatus));
	}

	@GetMapping("/user/{userId}/total-paid")
	public ResponseEntity<?> getTotalPaidByUserId(@PathVariable Long userId, Authentication authentication) {

		if (isAdmin(authentication)) {
			return ResponseEntity.ok(paymentService.getTotalPaidByUserId(userId));
		}

		User user = getCurrentUser(authentication);

		if (user == null) {
			return ResponseEntity.status(401).build();
		}

		if (!user.getUid().equals(userId)) {
			return ResponseEntity.status(403).body(Map.of("message", "Access denied"));
		}

		return ResponseEntity.ok(paymentService.getTotalPaidByUserId(user.getUid()));
	}

	@GetMapping("/countAllPayments")
	public ResponseEntity<?> countAllPayments(Authentication authentication) {

		if (!isAdmin(authentication)) {
			return ResponseEntity.status(403).build();
		}

		return ResponseEntity.ok(paymentService.countAllPayments());
	}

	@GetMapping("/count/successful")
	public ResponseEntity<?> countSuccessfulPayments(Authentication authentication) {

		if (!isAdmin(authentication)) {
			return ResponseEntity.status(403).build();
		}

		return ResponseEntity.ok(paymentService.countSuccessfulPayments());
	}

	@GetMapping("/count/failed")
	public ResponseEntity<?> countFailedPayments(Authentication authentication) {

		if (!isAdmin(authentication)) {
			return ResponseEntity.status(403).build();
		}

		return ResponseEntity.ok(paymentService.countFailedPayments());
	}

	@GetMapping("/count/pending")
	public ResponseEntity<?> countPendingPayments(Authentication authentication) {

		if (!isAdmin(authentication)) {
			return ResponseEntity.status(403).build();
		}

		return ResponseEntity.ok(paymentService.countPendingPayments());
	}

	@GetMapping("/summary")
	public ResponseEntity<?> getPaymentSummary(Authentication authentication) {

		if (!isAdmin(authentication)) {
			return ResponseEntity.status(403).build();
		}

		Map<String, Long> summary = new HashMap<>();

		summary.put("totalPayments", paymentService.countAllPayments());
		summary.put("successfulPayments", paymentService.countSuccessfulPayments());
		summary.put("failedPayments", paymentService.countFailedPayments());
		summary.put("pendingPayments", paymentService.countPendingPayments());

		return ResponseEntity.ok(summary);
	}

	private boolean isAdmin(Authentication authentication) {

		return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	}

	private User getCurrentUser(Authentication authentication) {

		return userRepository.findByEmail(authentication.getName());
	}
}