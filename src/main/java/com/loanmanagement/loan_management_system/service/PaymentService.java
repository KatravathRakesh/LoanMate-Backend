package com.loanmanagement.loan_management_system.service;

import java.util.List;

import com.loanmanagement.loan_management_system.entity.Payment;

public interface PaymentService {

	void addPayment(Payment payment);

	List<Payment> getAllPayments();

	Payment getPaymentById(Long paymentId);
	
//	get payment by UserID
	List<Payment> getPaymentsByUserId(Long userId);
	
//	get payment by LoanID
	List<Payment> getPaymentsByLoanId(Long LoanId);
	
//	get payment by PaymentStatus
	List<Payment> getPaymentsByStatus(String paymentStatus);
	
//	count paymentStatus
	long countPaymentsByStatus(String paymentStatus);
	
	Double getTotalPaidByUserId(Long userId);
	
//	count all Payments
	long countAllPayments();
	
//	count successful 
	long countSuccessfulPayments();
	
//	count failed
	long countFailedPayments();
	
//	count Pending
	long countPendingPayments();

}
