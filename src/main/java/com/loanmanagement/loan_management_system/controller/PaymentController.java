package com.loanmanagement.loan_management_system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loanmanagement.loan_management_system.entity.Payment;
import com.loanmanagement.loan_management_system.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/addPayments")
    public String addPayment(@Valid @RequestBody Payment payment) {
        paymentService.addPayment(payment);
        return "Payment added successfully";
    }

    @GetMapping("/GetAllPayments")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/findPayById/{paymentId}")
    public Payment getPaymentById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }
    
    @GetMapping("/userID/{userId}")
    public List<Payment> getPaymentsByUserId(@PathVariable Long userId) {
        return paymentService.getPaymentsByUserId(userId);
    }
    
    @GetMapping("/loanID/{loanId}")
    public List<Payment> getPaymentsByLoanId(@PathVariable Long loanId) {
        return paymentService.getPaymentsByLoanId(loanId);
    }
    
    @GetMapping("/status/{paymentStatus}")
    public List<Payment> getPaymentsByStatus(@PathVariable String paymentStatus) {
        return paymentService.getPaymentsByStatus(paymentStatus);
    }
    
    @GetMapping("/count/status/{paymentStatus}")
    public long countPaymentsByStatus(@PathVariable String paymentStatus) {
        return paymentService.countPaymentsByStatus(paymentStatus);
    }
    
    @GetMapping("/user/{userId}/total-paid")
    public Double getTotalPaidByUserId(@PathVariable Long userId) {
        return paymentService.getTotalPaidByUserId(userId);
    }
    
    @GetMapping("/countAllPayments")
    public long countAllPayments() {
        return paymentService.countAllPayments();
    }
    
    @GetMapping("/count/successful")
    public long countSuccessfulPayments() {
        return paymentService.countSuccessfulPayments();
    }
    
    @GetMapping("/count/failed")
    public long countFailedPayments() {
        return paymentService.countFailedPayments();
    }
    
    @GetMapping("/count/pending")
    public long countPendingPayments() {
        return paymentService.countPendingPayments();
    }
    
    @GetMapping("/summary")
    public Map<String, Long> getPaymentSummary() {
        Map<String, Long> summary = new HashMap<>();

        summary.put("totalPayments", paymentService.countAllPayments());
        summary.put("successfulPayments", paymentService.countSuccessfulPayments());
        summary.put("failedPayments", paymentService.countFailedPayments());
        summary.put("pendingPayments", paymentService.countPendingPayments());

        return summary;
    }
    
   
}