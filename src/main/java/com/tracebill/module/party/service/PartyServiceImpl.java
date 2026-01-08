package com.tracebill.module.party.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tracebill.module.party.dto.PartyDTO;
import com.tracebill.module.party.dto.PartyRegisterModel;
import com.tracebill.module.party.entity.BillingEntity;
import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.enums.PartyType;
import com.tracebill.module.party.exception.PartyNotFoundException;
import com.tracebill.module.party.repo.BillingEntityRepo;
import com.tracebill.module.party.repo.PartyRepo;
import com.tracebill.module.user.entity.User;
import com.tracebill.module.user.exception.UserNotFoundException;
import com.tracebill.module.user.service.UserService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService {

	private final UserService userService;

	private final PartyRepo partyRepo;

	private final BillingEntityRepo billingEntityRepo;

	@Override
	@Transactional
	public PartyDTO createParty(@Valid PartyRegisterModel model) {
		User user = null;

		if (model.getPartyType() != PartyType.RETAILER) {
			Optional<User> userOp = userService.getActiveUserOpByEmail(model.getEmail());
			if (userOp.isEmpty()) {
				log.error("User with email Id is inactive or not created");
				throw new UserNotFoundException();
			}
			user = userOp.get();
			if(!model.getPartyType().toString().equals(user.getRole().displayName())) {
				throw new IllegalStateException("Party Type is not matched with user");
			}
		}
		
		Party parent = null;
		
		if(model.getPartyType() != PartyType.MANUFACTURER) {
			
			Optional<Party> parentOp = this.getPartyOpById(model.getParentPartyId());
			if(parentOp.isEmpty()) {
				throw new PartyNotFoundException(model.getParentPartyId());
			}
			parent = parentOp.get();
				
		}
		
		BillingEntity billingEntity = this.createBillingEntity(model);
		
		Party party = Party.builder()
				.email(model.getEmail())
				.user(user)
				.billingEntity(billingEntity)
				.parent(parent)
				.type(model.getPartyType())
				.build();
		partyRepo.save(party);
		
		PartyDTO partyDTO = new PartyDTO(party.getEmail(), party.getType().toString(), billingEntity, party.getParent()==null ? null : parent.getBillingEntity().getLegalName());
		return partyDTO;

	}

	private Optional<Party> getPartyOpById(Long partyId) {
		if(partyId == null) throw new IllegalArgumentException("Party Id can not be null");
		return partyRepo.findById(partyId);
	}

	@Override
	public Optional<Party> getPartyOpByEmailId(String email) {
		if(email == null) throw new IllegalArgumentException("Party Email can not be null");
		return partyRepo.findByEmail(email);
	}

	private BillingEntity createBillingEntity(@Valid PartyRegisterModel model) {

		BillingEntity billingEntity = BillingEntity.builder().
				gstNo(model.getGstNo())
				.legalName(model.getLegalName())
				.phoneNumber(model.getPhoneNumber())
				.address(model.getAddress())
				.emailId(model.getEmail())
				.registered(model.getRegistered())
				.build();

		billingEntityRepo.save(billingEntity);
		return billingEntity;
	}

}
