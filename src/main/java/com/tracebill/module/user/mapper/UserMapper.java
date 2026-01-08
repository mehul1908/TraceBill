package com.tracebill.module.user.mapper;

import com.tracebill.module.auth.dto.RegisterUserModel;
import com.tracebill.module.user.dto.CreateUserModel;

public class UserMapper {

    private UserMapper() {} // Utility class, no instantiation

    public static CreateUserModel toCreateUserModel(RegisterUserModel model, String hashedPassword) {
        return new CreateUserModel(
            hashedPassword,
            model.getEmail(),
            model.getRole(),
            model.getWalletAddress()
        );
    }
}
