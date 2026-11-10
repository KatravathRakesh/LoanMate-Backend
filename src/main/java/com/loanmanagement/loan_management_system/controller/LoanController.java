package com.loanmanagement.loan_management_system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanmanagement.loan_management_system.entity.Loan;
import com.loanmanagement.loan_management_system.service.LoanService;

@RestController
@RequestMapping("/loans")
public class LoanController {

	@Autowired
	private LoanService loanService;
	
	@PostMapping("/addLoan")
	public String addLoan(@RequestBody Loan loan) {
		loanService.addLoan(loan);
		return "Loan added successfully";
	}
	
	@GetMapping("/getAllLoan")
	public List<Loan> findAllLoan(){
		return loanService.getAllLoans();
	}
	
	@GetMapping("/getLoan/{loanId}")
	public Loan findLoanById(@PathVariable Long loanId) {
		return loanService.getLoanById(loanId);
	}
	
	@GetMapping("/GetLoanByUserId/{userId}")
	public List<Loan> getLoansByUserId(@PathVariable Long userId) {
		return loanService.getLoanByUserId(userId);
	}
	
	@GetMapping("/GetLoanByStatus/{status}")
	public List<Loan> getLoansByUserId(@PathVariable String status) {
		return loanService.getLoanByStatus(status);
	}
	
	@GetMapping("/count")
	public long countLoans() {
	    return loanService.countLoans();
	}
	
	@GetMapping("/count/active")
	public long countActiveLoans() {
	    return loanService.countActiveLoans();
	}
	
	@PutMapping("/updatebyId/{loanId}/status")
	public String updateLoanStatus(@PathVariable Long loanId,@RequestBody String status) {
		loanService.updateLoanStatus(loanId, status);

	    return "Loan status updated successfully";
	    
	}
	
	@DeleteMapping("/deleteLoanById/{loanId}")
	public String deleteLoanByid(@PathVariable Long loanId) {
		loanService.deleteLoan(loanId);
		return "Loan  Deleted Successfully";
	}
	
	
	@GetMapping("/{loanId}/summary")
	public Map<String, Object> getLoanSummary(@PathVariable Long loanId) {

	    Loan loan = loanService.getLoanById(loanId);

	    Map<String, Object> summary = new HashMap<>();

	    summary.put("loanId", loan.getLoanId());
	    summary.put("loanAmount", loan.getLoanAmount());
	    summary.put("totalPaid", loan.getTotalPaid());
	    summary.put("remainingAmount", loan.getRemainingAmount());
	    summary.put("status", loan.getStatus());

	    return summary;
	}
	
	@GetMapping("/user/{userId}/active")
	public List<Loan> getActiveLoansByUserId(
	        @PathVariable Long userId) {

	    return loanService.getLoanByUserId(userId)
	            .stream()
	            .filter(loan -> "ACTIVE".equalsIgnoreCase(loan.getStatus()))
	            .toList();
	}
	
	@GetMapping("/user/{userId}/active/total")
	public Double getTotalActiveLoanAmount(
	        @PathVariable Long userId) {

	    return loanService.getLoanByUserId(userId)
	            .stream()
	            .filter(loan -> "ACTIVE".equalsIgnoreCase(loan.getStatus()))
	            .mapToDouble(loan -> loan.getLoanAmount() == null
	                    ? 0.0 : loan.getLoanAmount())
	            .sum();
	}
	
	
	@GetMapping("/user/{userId}/active/remaining")
	public Double getTotalRemainingAmount(
	        @PathVariable Long userId) {

	    return loanService.getLoanByUserId(userId)
	            .stream()
	            .filter(loan -> "ACTIVE".equalsIgnoreCase(loan.getStatus()))
	            .mapToDouble(loan -> loan.getRemainingAmount() == null
	                    ? 0.0 : loan.getRemainingAmount())
	            .sum();
	}
}
