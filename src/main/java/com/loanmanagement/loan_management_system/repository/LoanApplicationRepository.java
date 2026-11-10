package com.loanmanagement.loan_management_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loanmanagement.loan_management_system.entity.LoanApplication;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

	List<LoanApplication> findByUserId(Long userId);

	List<LoanApplication> findByStatus(String status);
	
	long countByStatus(String status);

}
