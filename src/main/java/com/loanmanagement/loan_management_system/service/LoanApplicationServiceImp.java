
package com.loanmanagement.loan_management_system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loanmanagement.loan_management_system.entity.Loan;
import com.loanmanagement.loan_management_system.entity.LoanApplication;
import com.loanmanagement.loan_management_system.repository.LoanApplicationRepository;
import com.loanmanagement.loan_management_system.repository.LoanRepository;

@Service
public class LoanApplicationServiceImp implements LoanApplicationService {

    @Autowired
    private LoanApplicationRepository loanRepo;

    @Autowired
    private LoanRepository loanRepository;

    // 1. Apply New Loan
    @Override
    public void applyLoan(LoanApplication loan) {

        loan.setApplicationId(null);
        loan.setStatus("PENDING");
        loan.setApplicationDate(null);

        loanRepo.save(loan);
    }

    // 2. Find All Applications
    @Override
    public List<LoanApplication> findAllLoan() {
        return loanRepo.findAll();
    }

    // 3. Find Application By ID
    @Override
    public LoanApplication findLoanById(Long applicationId) {

        return loanRepo.findById(applicationId).orElse(null);
    }

    // 4. Update Application Status
    @Override
    @Transactional
    public LoanApplication updateLoanStatus(Long applicationId, String status) {

        LoanApplication application = loanRepo.findById(applicationId)
                .orElseThrow(() ->
                        new RuntimeException("Loan application not found"));

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan status cannot be empty");
        }

        status = status.trim().toUpperCase();

        if (!status.equals("PENDING")
                && !status.equals("APPROVED")
                && !status.equals("REJECTED")) {

            throw new IllegalArgumentException("Invalid loan status");
        }

        application.setStatus(status);

        loanRepo.save(application);

        // Create a loan only when application is approved
        // Avoid duplicate loan creation
        if (status.equals("APPROVED")
                && !loanRepository.existsByApplicationId(applicationId)) {

            Loan loan = new Loan();

            loan.setApplicationId(application.getApplicationId());
            loan.setUserId(application.getUserId());
            loan.setLoanAmount(application.getLoanAmount());
            loan.setInterestRate(10.5);
            loan.setTenure(application.getTenure());
            loan.setStatus("ACTIVE");

            loan.setTotalPaid(0.0);
            loan.setRemainingAmount(application.getLoanAmount());

            loan.setLoanStartDate(LocalDate.now());

            loanRepository.save(loan);
        }

        return application;
    }

    // 5. Delete Application
    @Override
    public void deleteLoan(Long applicationId) {

        if (!loanRepo.existsById(applicationId)) {
            throw new RuntimeException("Loan application not found");
        }

        loanRepo.deleteById(applicationId);
    }

    // 6. Find Applications By User ID
    @Override
    public List<LoanApplication> findLoansByUserId(Long userId) {
        return loanRepo.findByUserId(userId);
    }

    // 7. Find Applications By Status
    @Override
    public List<LoanApplication> findLoanByStatus(String status) {

        return loanRepo.findByStatus(status.trim().toUpperCase());
    }

    // 8. Count Pending Applications
    @Override
    public long countPendingApplications() {
        return loanRepo.countByStatus("PENDING");
    }

    // 9. Count Approved Applications
    @Override
    public long countApprovedApplications() {
        return loanRepo.countByStatus("APPROVED");
    }

    // 10. Count Rejected Applications
    @Override
    public long countRejectedApplications() {
        return loanRepo.countByStatus("REJECTED");
    }

    // 11. Count All Applications
    @Override
    public long countAllApplications() {
        return loanRepo.count();
    }
}
