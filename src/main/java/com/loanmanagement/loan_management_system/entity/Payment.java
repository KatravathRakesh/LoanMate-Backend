package com.loanmanagement.loan_management_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@NotNull
	@Positive
	private Long loanId;

	@NotNull
	@Positive
	private Long userId;

	@NotNull
	@Positive
	private Double amount;

	@NotBlank
	@Pattern(
		    regexp = "^\\d{4}-\\d{2}-\\d{2}$",
		    message = "Payment date must use YYYY-MM-DD format"
		)
	private String paymentDate;

	@NotBlank
	@Pattern(
		    regexp = "SUCCESS|PENDING|FAILED",
		    message = "Payment status must be SUCCESS, PENDING, or FAILED"
		)
	private String paymentStatus;
	
	private String transactionReference;
}
