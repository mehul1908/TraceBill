package com.tracebill.module.user.service;


import java.util.Optional;

import com.tracebill.module.user.dto.CreateUserModel;
import com.tracebill.module.user.entity.User;


public interface UserService{

	User getUserById(Long userId);

	void saveUser(CreateUserModel createModel);

	User getActiveUserByEmailId(String email);

	Optional<User> getActiveUserOpByEmail(String email);

}
