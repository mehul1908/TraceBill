package com.tracebill.module.party.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.module.party.dto.BillingEntityRegisterModel;
import com.tracebill.module.party.entity.BillingEntity;
import com.tracebill.module.party.repo.BillingEntityRepo;

import jakarta.validation.Valid;

@Service
public class BillingEntityServiceImpl implements BillingEntityService{

	@Autowired
	private BillingEntityRepo billingEntityRepo;
	
	@Autowired
	private PartyService partyService;

	@Override
	public void createBillingEntity(@Valid BillingEntityRegisterModel model) {
		
		Long partyId = partyService.createParty(model.getEmail() , model.getParentPartyId() , model.getPartyType());
		
		BillingEntity billingEntity = BillingEntity.builder()
				.gstNo(model.getGstNo())
				.legalName(model.getLegalName())
				.phoneNumber(model.getPhoneNumber())
				.address(model.getAddress())
				.emailId(model.getEmail())
				.registered(model.getRegistered())
				.partyId(partyId)
				.build();
		
		billingEntityRepo.save(billingEntity);
	}

	@Override
	public BillingEntity getBillingEntityByPartyId(Long partyId) {
		return billingEntityRepo.findByPartyId(partyId)
				.orElseThrow(() -> new ResourceNotFoundException("Billing Enity with party not found : " + partyId));
	}
	
	
	
	
}
