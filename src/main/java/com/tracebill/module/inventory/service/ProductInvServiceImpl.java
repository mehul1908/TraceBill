package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.entity.ProductInventory;
import com.tracebill.module.inventory.repo.ProductInvRepo;

@Service
public class ProductInvServiceImpl implements ProductInvService {

	@Autowired
	private ProductInvRepo prodInvRepo;
	
	@Autowired
	private AuthenticatedUserProvider authenticatedUser;
	
	@Override
	public Long getProdInvByProdAndPartyOrCreate(Long productId , Long partyId) {
		Optional<ProductInventory> prodInvOp = prodInvRepo.findByProductInvIdAndOwnerId(productId , partyId);
		if(prodInvOp.isPresent())
			return prodInvOp.get().getProductInvId();
		
		Long ownerPartyId = authenticatedUser.getAuthenticatedParty();
		
		ProductInventory prodInv = ProductInventory.builder()
				.productId(productId)
				.qty(BigInteger.valueOf(0))
				.ownerId(ownerPartyId)
				.build();
		
		ProductInventory saved = prodInvRepo.save(prodInv);
		return saved.getProductInvId();
		
	}

	@Override
	public void addQuantity(Long prodInvId, BigInteger manufacturedQty) {
		
		Optional<ProductInventory> prodInvOp = prodInvRepo.findById(prodInvId);
		if(prodInvOp.isEmpty())
			throw new ResourceNotFoundException("Product Inventory with given Id is not found :" + prodInvId);
		ProductInventory prodInv = prodInvOp.get();
		
		BigInteger before = prodInv.getQty();
		BigInteger after = before.add(manufacturedQty);
		
		prodInv.setQty(after);
		prodInvRepo.save(prodInv);
		
		
		
	}

}
