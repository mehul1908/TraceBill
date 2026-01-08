package com.tracebill.module.user.dto;

import com.tracebill.module.user.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserModel {
	
	private String hashedPassword;
	
	private String email;
	
	private UserRole role;
	
	private String walletAddress;
	
}

