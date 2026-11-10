package com.loanmanagement.loan_management_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanmanagement.loan_management_system.entity.LoanApplication;
import com.loanmanagement.loan_management_system.service.LoanApplicationService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/loan")
public class LoanApplicationController {
	
	@Autowired
	LoanApplicationService loanService;
	
	@PostMapping("/apply")
	public String applyLoan(@Valid @RequestBody LoanApplication Loan) {
		loanService.applyLoan(Loan);
		
		return "Loan Application Submitted Successfully";
	}
	
	
	@GetMapping("/findAllLoan")
	public List<LoanApplication> findAllLoan() {
		return loanService.findAllLoan();
	}
	
	@GetMapping("/findById/{applicationId}")
	public LoanApplication findLoanById(@PathVariable Long applicationId) {
		return loanService.findLoanById(applicationId);
	}
	
	@PutMapping("/updateById/{applicationId}")
	public LoanApplication updateLoanStatus(@PathVariable Long applicationId, @RequestBody String status) {
		
		status = status.replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .replace("status:", "")
                .trim();
		
		return loanService.updateLoanStatus(applicationId, status);
	}
	
	@DeleteMapping("/deleteById/{applicationId}")
	public String deleteLoanById(@PathVariable Long applicationId ) {
		
		loanService.deleteLoan(applicationId);
		
		return "Loan Application Deleted Successfully";
	}
	
	@GetMapping("/user/{userId}")
	public List<LoanApplication> findLoanByuserId(@PathVariable Long userId){
		return loanService.findLoansByUserId(userId);
	}
	
	@GetMapping("/loan/status/{status}")
	public List<LoanApplication> findLoanByStatus(@PathVariable String status){
		return loanService.findLoanByStatus(status);
	}
	
	@GetMapping("/count/pending")
	public long countPendingApplications() {
	    return loanService.countPendingApplications();
	}
	
	
	@GetMapping("/count/Approved")
	public long countApprovedApplication() {
		return loanService.countApprovedApplications();
	}
	
	@GetMapping("/count/Rejected")
	public long countRejectedApplication() {
		return loanService.countRejectedApplications();
	}
	
	@GetMapping("/countAll")
	public long countAllApplication() {
		return loanService.countAllApplications();
	}
	
	@GetMapping("/status/approved")
	public List<LoanApplication> getApprovedApplications() {
	    return loanService.findLoanByStatus("APPROVED");
	}
	
}
