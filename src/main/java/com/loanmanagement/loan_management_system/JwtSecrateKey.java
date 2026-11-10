package com.loanmanagement.loan_management_system;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class JwtSecrateKey {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
	 	SecretKey secretKey = keyGenerator.generateKey();
	 	String jwtkey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
	 	System.out.println(jwtkey);
	}
}
