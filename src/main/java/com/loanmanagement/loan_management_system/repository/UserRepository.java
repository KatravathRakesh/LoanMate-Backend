package com.loanmanagement.loan_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.loanmanagement.loan_management_system.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);
	
	boolean existsByRole(String role);
	
	long countByRole(String role);

	boolean existsByEmail(String email);
}
