package com.loanmanagement.loan_management_system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loanmanagement.loan_management_system.entity.LoanApplication;
import com.loanmanagement.loan_management_system.repository.LoanApplicationRepository;
import com.loanmanagement.loan_management_system.entity.Loan;
import com.loanmanagement.loan_management_system.repository.LoanRepository;

@Service
public class LoanApplicationServiceImp implements LoanApplicationService {

	@Autowired
	LoanApplicationRepository loanRepo;

	@Autowired
	LoanRepository loanRepository;

	@Override
	public void applyLoan(LoanApplication loan) {
		loan.setStatus("PENDING");
		loanRepo.save(loan);

	}

	@Override
	public List<LoanApplication> findAllLoan() {
		return loanRepo.findAll();
	}

	@Override
	public LoanApplication findLoanById(Long applicationId) {
		return loanRepo.findById(applicationId).orElse(null);
	}

	 @Override
	 public LoanApplication updateLoanStatus(Long applicationId, String s) {

	     LoanApplication loanApplication =
	             loanRepo.findById(applicationId)
	                     .orElseThrow(() ->
	                             new RuntimeException("Loan application not found"));

	     if (s == null || s.trim().isEmpty()) {
	         throw new IllegalArgumentException("Loan status cannot be empty");
	     }

	     s = s.trim().toUpperCase();

	     if (!s.equals("PENDING")
	             && !s.equals("APPROVED")
	             && !s.equals("REJECTED")) {
	         throw new IllegalArgumentException("Invalid loan status");
	     }

	     loanApplication.setStatus(s);
	     loanRepo.save(loanApplication);

	     if (s.equals("APPROVED")
	             && !loanRepository.existsByApplicationId(applicationId)) {

	         Loan loan = new Loan();

	         loan.setApplicationId(loanApplication.getApplicationId());
	         loan.setUserId(loanApplication.getUserId());
	         loan.setLoanAmount(loanApplication.getLoanAmount());
	         loan.setInterestRate(10.5);
	         loan.setTenure(loanApplication.getTenure());
	         loan.setStatus("ACTIVE");

	         loan.setTotalPaid(0.0);
	         loan.setRemainingAmount(loan.getLoanAmount());
	         
	      // Set loan start date when approved
	         loan.setLoanStartDate(LocalDate.now());

	         loanRepository.save(loan);
	     }

	     return loanApplication;
	 }

	@Override
	public void deleteLoan(Long applicationId) {
		loanRepo.deleteById(applicationId);

	}

	@Override
	public List<LoanApplication> findLoansByUserId(Long userId) {
		return loanRepo.findByUserId(userId);
	}

	@Override
	public List<LoanApplication> findLoanByStatus(String status) {
		return loanRepo.findByStatus(status);
	}

	@Override
	public long countPendingApplications() {
		return loanRepo.countByStatus("PENDING");
	}

	@Override
	public long countApprovedApplications() {
		return loanRepo.countByStatus("Approved");
	}

	@Override
	public long countRejectedApplications() {
		return loanRepo.countByStatus("Rejected");
	}

	@Override
	public long countAllApplications() {
		return loanRepo.count();
	}

}
