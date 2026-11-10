package com.loanmanagement.loan_management_system.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplication {
       
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long applicationId;

	@NotNull
    private Long userId;
	
	@NotBlank
    private String loanType;
	
    @NotNull
    @Min(1)
    private Double loanAmount;
    
    @NotNull
    @Min(1)
    private Integer tenure;
    
    @NotBlank
    private String purpose;
    
    private String status;
    
    @Column(name = "application_date", updatable = false)
    private LocalDate applicationDate;

    @PrePersist
    public void setApplicationDate() {
        if (this.applicationDate == null) {
            this.applicationDate = LocalDate.now();
        }
    }
}
