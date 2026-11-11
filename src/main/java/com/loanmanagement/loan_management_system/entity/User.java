package com.loanmanagement.loan_management_system.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;


@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long uid;
	String uname;
	@Column(nullable = false, unique = true)
	private String email;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	String mobile;
	String address;
	String role;
	
	@Column(name = "dob")
	private LocalDate dob;

	@Column(name = "gender")
	private String gender;

	@Column(name = "pan_number")
	private String panNumber;

	@Column(name = "aadhaar_number")
	private String aadhaarNumber;
	
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String profileImage;
}
