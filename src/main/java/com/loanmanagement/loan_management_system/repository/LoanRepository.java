package com.loanmanagement.loan_management_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loanmanagement.loan_management_system.entity.Loan;

@Repository
public interface LoanRepository  extends JpaRepository<Loan, Long>{

	boolean existsByApplicationId(Long applicationId);
	
	List<Loan> findByUserId(Long userId);
	
	List<Loan> findByStatus(String status);
	
	long countByStatus(String status);
	
	Loan findByLoanIdAndUserId(Long loanId, Long userId);
	
	
	
}
