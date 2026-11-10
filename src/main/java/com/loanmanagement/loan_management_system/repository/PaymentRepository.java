package com.loanmanagement.loan_management_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.loanmanagement.loan_management_system.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByUserId(Long userId);
	
	List<Payment> findByLoanId(Long loanId);
	
	List<Payment> findByPaymentStatus(String paymentStatus);
	
	long countByPaymentStatus(String paymentStatus);
	
	@Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.userId = :userId AND p.paymentStatus = 'SUCCESS'")
	Double getTotalPaidByUserId(@Param("userId") Long userId);
	
	boolean existsByTransactionReference(String transactionReference);
}
