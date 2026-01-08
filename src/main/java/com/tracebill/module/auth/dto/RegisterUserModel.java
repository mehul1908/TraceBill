package com.tracebill.module.auth.dto;

import com.tracebill.module.user.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserModel {
	
	
	@NotBlank(message = "Password can not be blank")
    @Size(min = 8, max = 64)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
        message = "Password must contain upper, lower, number, and special character"
    )
	private String password;
	
	@NotBlank(message = "Email can not be blank")
	@Email(message = "Email must be in proper format")
	private String email;
	
	@NotNull(message="Role can not be null")
	private UserRole role;
	
	@NotBlank(message = "Wallet Address can not be null")
	private String walletAddress;
}
