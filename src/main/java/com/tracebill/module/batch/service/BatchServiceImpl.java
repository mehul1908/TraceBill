package com.tracebill.module.batch.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.exception.UnauthorizedUserException;
import com.tracebill.module.audit.service.AuditLogService;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.batch.dto.BatchRegisterModel;
import com.tracebill.module.batch.entity.Batch;
import com.tracebill.module.batch.repo.BatchRepo;
import com.tracebill.module.inventory.service.BatchInvService;
import com.tracebill.module.inventory.service.ProductInvService;
import com.tracebill.module.production.service.FactoryService;
import com.tracebill.module.production.service.ProductService;
import com.tracebill.util.SequenceGeneratorService;

import jakarta.transaction.Transactional;

@Service
public class BatchServiceImpl implements BatchService {

	@Autowired
	private BatchRepo batchRepo;
	
	@Autowired
	private FactoryService factoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private AuditLogService auditService;
	
	@Autowired
	private SequenceGeneratorService sequenceGenerator;
	
	@Autowired
	private ProductInvService prodInvService;
	
	@Autowired
	private BatchInvService batchInvService;
	
	@Autowired
	private AuthenticatedUserProvider authenticatedUser;
	
	@Transactional
	@Override
	@PreAuthorize("hasRole('MANUFACTURER')")
	public String createBatch(BatchRegisterModel model) {
		
		Long partyId = authenticatedUser.getAuthenticatedParty();

		if (!factoryService.isFactoryOwnedBy(model.getFactoryId(), partyId)) {
		    throw new UnauthorizedUserException("Factory does not belong to authenticated manufacturer");
		}

		
		if(!productService.existById(model.getProductId())) {
			throw new ResourceNotFoundException("Product not found : " + model.getProductId());
		}
		
		Integer nextSeq = sequenceGenerator.nextBatchSeq(model.getProductId(), model.getFactoryId(), model.getManufacturedDate());
		String datePart = model.getManufacturedDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String batchNo = String.format("B-%s%03d%03d%02d", datePart, model.getProductId(), model.getFactoryId(), nextSeq);

        Batch batch = Batch.builder()
        		.batchNo(batchNo)
        		.manufacturedDate(model.getManufacturedDate())
        		.expiryDate(model.getExpiryDate())
        		.factoryId(model.getFactoryId())
        		.productId(model.getProductId())
        		.manufacturedQty(model.getManufacturedQty())
        		.build();
        
        Batch saved = batchRepo.save(batch);
                
        Long prodInvId = prodInvService.getProdInvByProdAndPartyOrCreate(model.getProductId() , partyId);
        
        Long batchInvId = batchInvService.createBatchInventory(saved.getBatchId() , prodInvId , model.getManufacturedQty());
        
        return saved.getBatchNo();
	}

}
