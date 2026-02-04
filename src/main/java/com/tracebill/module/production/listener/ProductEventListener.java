package com.tracebill.module.production.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.enums.BlockchainIntentType;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.blockchain.service.BlockchainService;
import com.tracebill.module.production.entity.Product;
import com.tracebill.module.production.record.ProductCreationEvent;
import com.tracebill.module.production.repo.ProductRepo;
import com.tracebill.module.production.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventListener {

	private final ProductService productService;
	
	private final BlockchainService blockchainService;
	
	private final BlockchainIntentRepo intentRepo;
	
	private final AuthenticatedUserProvider authenticatedUser;
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductCreation(ProductCreationEvent event) {
		
		log.info("IN listener product");
		BlockchainIntent intent =  BlockchainIntent.builder()
				.intentType(BlockchainIntentType.PRODUCT_REGISTER)
				.referenceType("PRODUCT")
				.referenceId(event.productId())
				.dataHash(event.prodHash())
				.fromPartyId(event.partyId())
				.toPartyId(event.partyId())
				.status(BlockchainIntentStatus.PENDING)
				.build();
		
		intentRepo.save(intent);
		
	}
}
