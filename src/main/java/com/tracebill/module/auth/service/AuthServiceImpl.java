package com.tracebill.module.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tracebill.config.JWTUtils;
import com.tracebill.dto.LoginResponse;
import com.tracebill.module.auth.dto.LoginUserModel;
import com.tracebill.module.auth.dto.RegisterUserModel;
import com.tracebill.module.auth.exception.UserIdAndPasswordNotMatchException;
import com.tracebill.module.user.dto.CreateUserModel;
import com.tracebill.module.user.entity.User;
import com.tracebill.module.user.mapper.UserMapper;
import com.tracebill.module.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	
	private final UserService userService;
	
	private final PasswordEncoder passEncoder;
	
	private final JWTUtils jwtUtils;
	

	@Override
	public LoginResponse authenticate(@Valid LoginUserModel model) {
		User user = userService.getActiveUserByEmailId(model.getEmail());
		if(passEncoder.matches(model.getPassword(), user.getPassword())) {
			String token = jwtUtils.generateToken(user.getEmail());
			LoginResponse lr = new LoginResponse(user.getId()  , user.getEmail(), user.getRole().toString() , token);
			log.info("User Log in successfully");
			return lr;
		}
		log.error("Password and Id does not match");
		throw new UserIdAndPasswordNotMatchException();
		
	}


	@Override
	public void createUser(@Valid RegisterUserModel model) {
		
		String hashedPassword = passEncoder.encode(model.getPassword());
		
		CreateUserModel createModel = UserMapper.toCreateUserModel(model, hashedPassword);
		
		userService.saveUser(createModel);
		
	}
}
