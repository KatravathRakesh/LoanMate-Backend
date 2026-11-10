package com.loanmanagement.loan_management_system.service;

import java.util.List;

import com.loanmanagement.loan_management_system.entity.Loan;

public interface LoanService {

//	TO Save A New Loan
	void addLoan(Loan loan);
	
//	To Get All Loan
	List<Loan> getAllLoans();
	
//	Find One Loan By it's ID
	Loan getLoanById(Long loanId);
	
//	 Find loan belonging to a specific customer
	Loan getLoanByIdAndUserId(Long loanId, Long userId);
	
//	Update Status
	Loan updateLoanStatus(Long loanId, String status);
	
//	Delete loan 
	void deleteLoan(Long loanId);
	
//	Get Loan By UserId
	
	List<Loan> getLoanByUserId(Long userId);
	
//	find Loan By status
	
	List<Loan> getLoanByStatus(String status);
	
//	CountAll
	long countLoans();
	
//	Count status 
	long countActiveLoans();
	
	
	void updateLoanPayment(Long loanId, Double amount);
	
	
}
