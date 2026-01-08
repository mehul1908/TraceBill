package com.tracebill.module.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tracebill.module.auth.entity.SecurityUser;
import com.tracebill.module.user.entity.User;
import com.tracebill.module.user.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements UserDetailsService{

	private final UserRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email));
        SecurityUser secUser = new SecurityUser(user);
        return secUser;
    }

}
