package com.loanmanagement.loan_management_system.service;

import java.util.List;

import com.loanmanagement.loan_management_system.entity.LoanApplication;

public interface LoanApplicationService {

//	Apply New Loan
	void applyLoan(LoanApplication loan);

//	Find All Loan
	List<LoanApplication> findAllLoan();

//	Find By ID

	LoanApplication findLoanById(Long applicationId);

//	Updating Status using ID
	
	LoanApplication updateLoanStatus(Long applicationId, String status);
	
//	Delete Loan
	void deleteLoan(Long applicationId);
	
	
//	Find Loan By User ID
	List<LoanApplication> findLoansByUserId(Long userId);
	
//	Find Loan By Status
	List<LoanApplication> findLoanByStatus(String status);
	
//	count pendingApplication 
	long countPendingApplications();
	
//	count Approved Application
	long countApprovedApplications();
	
//	count Rejected Application
	long countRejectedApplications();
	
//	count All Application like Approved/Reject/Pending
	long countAllApplications();
}








