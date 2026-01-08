package com.tracebill.module.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.dto.LoginResponse;
import com.tracebill.module.auth.dto.LoginUserModel;
import com.tracebill.module.auth.dto.RegisterUserModel;
import com.tracebill.module.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
		
	
	
	//Register a new user
	@PostMapping("/register")
	public ResponseEntity<ApiResponse> saveUser(@RequestBody @Valid RegisterUserModel model){
		authService.createUser(model);
		log.info("User is saved");
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, null, "User is created successfully"));
	}
	
	//Login a user
	@PostMapping("/login")
	public ResponseEntity<ApiResponse> loginUser(@RequestBody @Valid LoginUserModel model){
		LoginResponse lr = authService.authenticate(model);
		return ResponseEntity.ok(new ApiResponse(true , lr , "User is successfully logged in"));

		
	}
}
