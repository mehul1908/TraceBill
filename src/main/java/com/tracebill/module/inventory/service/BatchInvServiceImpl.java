package com.tracebill.module.inventory.service;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.entity.BatchInventory;
import com.tracebill.module.inventory.repo.BatchInvRepo;

import jakarta.transaction.Transactional;

@Service
public class BatchInvServiceImpl implements BatchInvService {

	@Autowired
	private AuthenticatedUserProvider authenticatedUser;
	
	@Autowired
	private BatchInvRepo batchInvRepo;
	
	@Autowired
	private ProductInvService prodInvService;
	
	@Override
	@Transactional
	public Long createBatchInventory(Long batchId, Long prodInvId, BigInteger manufacturedQty) {
		
		Long partyOwnerId = authenticatedUser.getAuthenticatedParty();
		
		BatchInventory batchInv = BatchInventory.builder()
				.batchId(batchId)
				.qty(manufacturedQty)
				.ownerId(partyOwnerId)
				.build();
		
		BatchInventory saved = batchInvRepo.save(batchInv);
		
		prodInvService.addQuantity(prodInvId , manufacturedQty);
		
		return saved.getBatchInvId();
		
		
	}

}
