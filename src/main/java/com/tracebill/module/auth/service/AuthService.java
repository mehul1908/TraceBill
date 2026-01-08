package com.tracebill.module.auth.service;

import com.tracebill.dto.LoginResponse;
import com.tracebill.module.auth.dto.LoginUserModel;
import com.tracebill.module.auth.dto.RegisterUserModel;

import jakarta.validation.Valid;

public interface AuthService {

	void createUser(@Valid RegisterUserModel model);

	LoginResponse authenticate(@Valid LoginUserModel model);

}
