package com.tracebill.module.batch.service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.exception.UnauthorizedUserException;
import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.audit.service.AuditLogService;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.batch.dto.BatchRegisterModel;
import com.tracebill.module.batch.entity.Batch;
import com.tracebill.module.batch.repo.BatchRepo;
import com.tracebill.module.inventory.service.BatchInvService;
import com.tracebill.module.inventory.service.InventoryTxnService;
import com.tracebill.module.inventory.service.ProductInvService;
import com.tracebill.module.production.entity.Product;
import com.tracebill.module.production.service.FactoryService;
import com.tracebill.module.production.service.ProductService;
import com.tracebill.util.HashService;
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
	private SequenceGeneratorService sequenceGenerator;

	@Autowired
	private ProductInvService prodInvService;

	@Autowired
	private BatchInvService batchInvService;

	@Autowired
	private AuthenticatedUserProvider authenticatedUser;

	@Autowired
	private InventoryTxnService inventoryTxnService;

	@Autowired
	private AuditLogService auditService;

	@Autowired
	private HashService hashService;

	@Transactional
	@Override
	@PreAuthorize("hasRole('MANUFACTURER')")
	public String createBatch(BatchRegisterModel model) {

		Long partyId = authenticatedUser.getAuthenticatedParty();

		if (!factoryService.isFactoryOwnedBy(model.getFactoryId(), partyId)) {
			throw new UnauthorizedUserException("Factory does not belong to authenticated manufacturer");
		}

		Product product = productService.getProductById(model.getProductId());

		Integer nextSeq = sequenceGenerator.nextBatchSeq(model.getProductId(), model.getFactoryId(),
				model.getManufacturedDate());
		String datePart = model.getManufacturedDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
		String batchNo = String.format("B-%s%03d%03d%02d", datePart, model.getProductId(), model.getFactoryId(),
				nextSeq);

		Batch batch = Batch.builder().batchNo(batchNo).manufacturedDate(model.getManufacturedDate())
				.expiryDate(model.getExpiryDate()).factoryId(model.getFactoryId()).productId(model.getProductId())
				.manufacturedQty(model.getManufacturedQty()).build();
		String batchHash = hashService.generateBatchHash(batchNo, product.getProductHash(), model.getFactoryId(),
				model.getManufacturedDate(), Long.valueOf(1));
		batch.setBatchHash(batchHash);
		Batch saved = batchRepo.save(batch);

		Long prodInvId = prodInvService.getProdInvByProdAndPartyOrCreate(model.getProductId(), partyId);

		batchInvService.createBatchInventory(saved.getBatchId(), prodInvId, model.getManufacturedQty());

		inventoryTxnService.recordProduction(saved.getBatchId(), model.getProductId(), saved.getManufacturedQty());
		auditService.create(AuditAction.CREATED, "Batch Created : " + saved.getBatchNo());
		return saved.getBatchNo();
	}

	@Override
	public Batch getBatchByBatchId(Long batchId) {
		return batchRepo.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch with given id not found" + batchId));
	}

	@Override
	public String getBatchHashById(Long batchId) {
		Optional<Batch> batchOp = batchRepo.findById(batchId);
		if (batchOp.isPresent())
			return batchOp.get().getBatchHash();
		throw new ResourceNotFoundException("Batch with given id not found" + batchId);
	}

}
