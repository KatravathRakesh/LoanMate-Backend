package com.loanmanagement.loan_management_system.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long loanId;
	
	private Long applicationId;
	private Long userId;
	private Double loanAmount;
	private Double interestRate;
	private Integer tenure;
	private String status;
	
	private Double totalPaid;
	private Double remainingAmount;
	
	private LocalDate loanStartDate;
}
