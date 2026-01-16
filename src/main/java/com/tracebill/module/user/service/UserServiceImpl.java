package com.tracebill.module.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.service.PartyService;
import com.tracebill.module.user.dto.CreateUserModel;
import com.tracebill.module.user.entity.User;
import com.tracebill.module.user.enums.UserRole;
import com.tracebill.module.user.exception.UserAlreadyCreatedException;
import com.tracebill.module.user.exception.UserNotFoundException;
import com.tracebill.module.user.repo.UserRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PartyService partyService;
	
	
	@Override
	//Return the User by the 'id'
	public User getUserById(Long userId) {
		   return userRepo.findById(userId)
		              .orElseThrow(() -> new UserNotFoundException(userId));
	}


	@Override
	public User getActiveUserByEmailId(String email) {
		Optional<User> userOp = userRepo.findByEmailAndActive(email , true);
		if(userOp.isPresent()) return userOp.get();
		throw new UserNotFoundException(email);
	}


	@Override
	public Optional<User> getActiveUserOpByEmail(String email) {
		return userRepo.findByEmailAndActive(email , true);
	}


	@Override
	public void saveUser(User user) {
		userRepo.save(user);
	}


	@Override
	public void saveUser(CreateUserModel model) {
		Optional<User> userOp = userRepo.findByEmail(model.getEmail());
		if(userOp.isPresent()) {
			log.error("User is already created : {} " , model.getEmail());
			throw new UserAlreadyCreatedException(model.getEmail());
		}
		
		Long partyId = null;
		if(model.getRole() != UserRole.ROLE_ADMIN && model.getRole() != UserRole.ROLE_TRANSPORTER) {
			Party party = partyService.getPartyByEmail(model.getEmail());
			partyId = party.getPartyId();
			if(!model.getEmail().equals(party.getEmail())) {
				throw new IllegalArgumentException("Email Id does not match with party");
			}
		}
		
		
		
		User savedUser = User.builder().
				email(model.getEmail()).
				password(model.getHashedPassword()).
				role(model.getRole()).
				walletAddress(model.getWalletAddress()).
				partyId(partyId).
				build();
		
		userRepo.save(savedUser);
		log.info("user is saved");
		return;
		
	}


}
