package com.tracebill.module.blockchain.processor;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlockchainIntentScheduler {

	private final BlockchainIntentRepo intentRepo;
	private final BlockchainIntentProcessor intentProcessor;

	@Scheduled(fixedDelayString = "${blockchain.intent.poll-interval:10000}")
	public void pollAndProcessIntents() {
		
			List<BlockchainIntent> pendingIntents = intentRepo
					.findTop20ByStatusOrderByCreatedAt(BlockchainIntentStatus.PENDING);

			for (BlockchainIntent intent : pendingIntents) {
				log.info("⏳ Processing intent id={} type={}", intent.getId(), intent.getIntentType());
				try {
					intentProcessor.process(intent);
				} catch (Exception ex) {
					intent.setStatus(BlockchainIntentStatus.FAILED);
					intentRepo.save(intent);
					log.error("Failed to process intent id={}", intent.getId(), ex);
				}
			}

		

	}
}
