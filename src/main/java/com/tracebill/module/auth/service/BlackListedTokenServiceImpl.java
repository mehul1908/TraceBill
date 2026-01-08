package com.tracebill.module.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tracebill.module.auth.entity.BlackListedToken;
import com.tracebill.module.auth.repo.BlackListedTokenRepo;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class BlackListedTokenServiceImpl implements BlackListedTokenService{

	private final BlackListedTokenRepo tokenRepo;
	
	@Override
	public boolean isTokenBlackListed(String jwt) {
		Optional<BlackListedToken> token = tokenRepo.findById(jwt);
		return token.isPresent();
	}

	@Override
	public void logout(String token) {
		
		BlackListedToken blacklisted = new BlackListedToken(token);
		tokenRepo.save(blacklisted);
		
	}

}
