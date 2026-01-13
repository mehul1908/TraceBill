package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.inventory.entity.BatchInventory;
import com.tracebill.module.inventory.entity.ProductInventory;
import com.tracebill.module.inventory.exception.InsufficientStockException;
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

	@Override
	@Transactional
	public List<BatchQuantityDTO> getBatchAndQuantityByProductAndQuantity(Long productId, BigInteger qty) {
		
		List<BatchQuantityDTO> dtos = new ArrayList<>();
		
		Long ownerId = authenticatedUser.getAuthenticatedParty();
		
		ProductInventory productInv = prodInvService.getProdInvByProdAndParty(productId, ownerId);
		
		if(productInv.getQty().compareTo(qty) < 0) {
			throw new InsufficientStockException("Insufficient Stock for product : " + productId);
		}
		
		List<BatchInventory> batchInvs = batchInvRepo.findAvailableBatchesFIFO(productId, ownerId);
		
		if (batchInvs.isEmpty()) {
	        throw new InsufficientStockException("No stock available for product: " + productId);
	    }
		
		BigInteger remainingQty = qty;
		
		for(BatchInventory batchInv : batchInvs) {
			
			BigInteger stock = batchInv.getQty();
			if(stock.compareTo(remainingQty) >= 0) {
				batchInv.subtractStock(remainingQty);
				batchInvRepo.save(batchInv);
				dtos.add(new BatchQuantityDTO(batchInv.getBatchId(), remainingQty));
				break;
			}else {
				batchInv.subtractStock(stock);
				remainingQty = remainingQty.subtract(stock);
				batchInvRepo.save(batchInv);
				dtos.add(new BatchQuantityDTO(batchInv.getBatchId(), stock));
			}
			
			if(remainingQty.compareTo(BigInteger.ZERO) == 0) {
				break;
			}
		}
		
		productInv.subtractStock(qty);
		prodInvService.save(productInv);
		
		return dtos;
	}

}
