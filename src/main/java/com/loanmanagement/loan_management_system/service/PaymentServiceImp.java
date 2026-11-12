package com.loanmanagement.loan_management_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loanmanagement.loan_management_system.entity.Loan;
import com.loanmanagement.loan_management_system.entity.Payment;
import com.loanmanagement.loan_management_system.repository.LoanRepository;
import com.loanmanagement.loan_management_system.repository.PaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImp implements PaymentService {

	@Autowired
	PaymentRepository payRepo;

	@Autowired
	private LoanService loanService;

	@Autowired
	private LoanRepository loanRepo;

	@Override
	@Transactional
	public void addPayment(Payment payment) {

	    if (payment.getTransactionReference() == null
	            || payment.getTransactionReference().isBlank()) {
	        throw new RuntimeException("Transaction reference is required");
	    }

	    if (payRepo.existsByTransactionReference(
	            payment.getTransactionReference())) {
	        throw new RuntimeException("Duplicate transaction reference");
	    }

	    if (payment.getAmount() == null || payment.getAmount() <= 0) {
	        throw new RuntimeException("Payment amount must be greater than zero");
	    }

	    String status = payment.getPaymentStatus();

	    if (status == null ||
	            !("SUCCESS".equalsIgnoreCase(status)
	            || "PENDING".equalsIgnoreCase(status)
	            || "FAILED".equalsIgnoreCase(status))) {
	        throw new RuntimeException(
	                "Payment status must be SUCCESS, PENDING, or FAILED");
	    }

	    Loan loan = loanRepo.findById(payment.getLoanId())
	            .orElseThrow(() -> new RuntimeException("Loan not found"));

	    if (!loan.getUserId().equals(payment.getUserId())) {
	        throw new RuntimeException("Loan does not belong to this user");
	    }

	    if ("SUCCESS".equalsIgnoreCase(status)) {

	        if (!"ACTIVE".equalsIgnoreCase(loan.getStatus())) {
	            throw new RuntimeException(
	                    "Payments are allowed only for active loans");
	        }

	        if (loan.getRemainingAmount() == null
	                || payment.getAmount() > loan.getRemainingAmount()) {
	            throw new RuntimeException(
	                    "Payment amount cannot exceed the remaining loan balance");
	        }

	        loanService.updateLoanPayment(
	                payment.getLoanId(), payment.getAmount());
	    }

	    payRepo.save(payment);
	}

	@Override
	public List<Payment> getAllPayments() {
		return payRepo.findAll();
	}

	@Override
	public Payment getPaymentById(Long paymentId) {
		return payRepo.findById(paymentId)
				.orElseThrow(() ->
                 new RuntimeException("Payment not found with ID: " + paymentId));
	}

	@Override
	public List<Payment> getPaymentsByUserId(Long userId) {
		return payRepo.findByUserId(userId);
	}

	@Override
	public List<Payment> getPaymentsByLoanId(Long LoanId) {
		return payRepo.findByLoanId(LoanId);
	}

	@Override
	public List<Payment> getPaymentsByStatus(String paymentStatus) {
		return payRepo.findByPaymentStatus(paymentStatus);
	}

	@Override
	public long countPaymentsByStatus(String paymentStatus) {
		return payRepo.countByPaymentStatus(paymentStatus);
	}

	@Override
	public Double getTotalPaidByUserId(Long userId) {
		return payRepo.getTotalPaidByUserId(userId);
	}

	@Override
	public long countAllPayments() {
		return payRepo.count();
	}

	@Override
	public long countSuccessfulPayments() {
		 return payRepo.countByPaymentStatus("SUCCESS");
	}

	@Override
	public long countFailedPayments() {
		 return payRepo.countByPaymentStatus("FAILED");
	}

	@Override
	public long countPendingPayments() {
		 return payRepo.countByPaymentStatus("PENDING");
	}
 
	@Override
	@Transactional
	public Payment approvePayment(Long paymentId) {

	    // 1. Find payment
	    Payment payment = payRepo.findById(paymentId)
	            .orElseThrow(() -> new RuntimeException("Payment not found"));

	    // 2. Only pending payments can be approved
	    if (!"PENDING".equalsIgnoreCase(payment.getPaymentStatus())) {
	        throw new RuntimeException("Only pending payments can be approved");
	    }

	    // 3. Find associated loan
	    Loan loan = loanRepo.findById(payment.getLoanId())
	            .orElseThrow(() -> new RuntimeException("Loan not found"));

	    // 4. Check loan ownership
	    if (!loan.getUserId().equals(payment.getUserId())) {
	        throw new RuntimeException("Payment and loan customer mismatch");
	    }

	    // 5. Check loan status
	    if (!"ACTIVE".equalsIgnoreCase(loan.getStatus())) {
	        throw new RuntimeException("Loan is not active");
	    }

	    // 6. Check remaining balance
	    if (loan.getRemainingAmount() == null
	            || payment.getAmount() > loan.getRemainingAmount()) {
	        throw new RuntimeException("Payment exceeds remaining loan amount");
	    }

	    // 7. Update loan balance
	    loanService.updateLoanPayment(payment.getLoanId(), payment.getAmount());

	    // 8. Mark payment successful
	    payment.setPaymentStatus("SUCCESS");

	    return payRepo.save(payment);
	}
	
}
