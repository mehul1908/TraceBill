package com.tracebill.module.batch.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.batch.entity.Batch;
import com.tracebill.module.batch.record.BatchCreationEvent;
import com.tracebill.module.batch.service.BatchService;
import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.enums.BlockchainIntentType;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.blockchain.service.BlockchainService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchEventListener {

	private final BlockchainIntentRepo intentRepo;
	private final BatchService batchService;
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleBatchCreation(BatchCreationEvent event) {
		Batch batch = batchService.getBatchByBatchId(event.batchId());
		
		
		log.info("IN listener product");
		BlockchainIntent intent =  BlockchainIntent.builder()
				.intentType(BlockchainIntentType.BATCH_CREATE)
				.referenceType("BATCH")
				.referenceId(batch.getBatchId())
				.dataHash(batch.getBatchHash())
				.fromPartyId(event.partyId())
				.toPartyId(event.partyId())
				.status(BlockchainIntentStatus.PENDING)
				.build();
		
		intentRepo.save(intent);
		
	}
	
	
}
