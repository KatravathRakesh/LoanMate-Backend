package com.loanmanagement.loan_management_system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loanmanagement.loan_management_system.entity.Loan;
import com.loanmanagement.loan_management_system.repository.LoanRepository;

@Service
public class LoanServiceImp implements LoanService {

	@Autowired
	LoanRepository LoanRepo;

	@Override
	public void addLoan(Loan loan) {

		if (loan.getLoanStartDate() == null) {
			loan.setLoanStartDate(LocalDate.now());
		}

		if (loan.getTotalPaid() == null) {
			loan.setTotalPaid(0.0);
		}

		if (loan.getRemainingAmount() == null) {
			loan.setRemainingAmount(loan.getLoanAmount());
		}

		LoanRepo.save(loan);
	}

	@Override
	public List<Loan> getAllLoans() {
		return LoanRepo.findAll();
	}

	@Override
	public Loan getLoanById(Long loanId) {
		return LoanRepo.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
	}

	@Override
	public Loan updateLoanStatus(Long loanId, String status) {

		Loan loan = LoanRepo.findById(loanId)
				.orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));

		if (status == null || status.isBlank()) {
			throw new RuntimeException("Loan status is required");
		}

		loan.setStatus(status.trim().toUpperCase());

		return LoanRepo.save(loan);
	}

	@Override
	public void deleteLoan(Long loanId) {

		Loan loan = LoanRepo.findById(loanId)
				.orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));

		LoanRepo.delete(loan);
	}

	@Override
	public List<Loan> getLoanByUserId(Long userId) {
		return LoanRepo.findByUserId(userId);
	}

	@Override
	public List<Loan> getLoanByStatus(String status) {
		return LoanRepo.findByStatus(status);
	}

	@Override
	public long countLoans() {
		return LoanRepo.count();
	}

	@Override
	public long countActiveLoans() {
		return LoanRepo.countByStatus("ACTIVE");
	}

	@Override
	public void updateLoanPayment(Long loanId, Double amount) {

		Loan loan = LoanRepo.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));

		double totalPaid = loan.getTotalPaid() == null ? 0.0 : loan.getTotalPaid();

		double loanAmount = loan.getLoanAmount() == null ? 0.0 : loan.getLoanAmount();

		if (amount == null || amount <= 0) {
			throw new RuntimeException("Payment amount must be greater than zero");
		}

		double remainingAmount = loan.getRemainingAmount() == null ? loanAmount - totalPaid : loan.getRemainingAmount();

		if (amount > remainingAmount) {
			throw new RuntimeException("Payment exceeds remaining loan amount");
		}

		loan.setTotalPaid(totalPaid + amount);
		loan.setRemainingAmount(loanAmount - loan.getTotalPaid());

		LoanRepo.save(loan);
	}

}
