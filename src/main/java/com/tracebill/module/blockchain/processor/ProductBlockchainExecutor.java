package com.tracebill.module.blockchain.processor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.tracebill.module.batch.service.BatchService;
import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.entity.BlockchainTransaction;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.blockchain.repo.BlockchainTxnRepo;
import com.tracebill.module.blockchain.service.BlockchainService;
import com.tracebill.module.production.entity.Product;
import com.tracebill.module.production.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductBlockchainExecutor {

	private final ProductService productService;
	private final BlockchainService blockchainService;
	private final BlockchainIntentRepo intentRepo;
    private final BlockchainTxnRepo blockchainTransactionRepo;
	
    @Transactional
    public void processProductCreation(BlockchainIntent intent) {
    		Product product = productService.getProductById(intent.getReferenceId());
    		TransactionReceipt receipt=blockchainService.productCreation(product.getProductHash());
    		BlockchainTransaction tx = BlockchainTransaction.builder()
                    .intentId(intent.getId())
                    .txHash(receipt.getTransactionHash().toLowerCase())
                    .blockNumber(receipt.getBlockNumber().longValue())
                    .contractAddress(receipt.getTo())
                    .submittedWallet(receipt.getFrom())
                    .eventName("PRODUCT_REGISTER")
                    .submittedAt(LocalDateTime.now())
                    .build();
    		blockchainTransactionRepo.save(tx);
    		intent.setDataHash(product.getProductHash());
    		intent.setStatus(BlockchainIntentStatus.SUBMITTED);
    		intentRepo.save(intent);
    }
}
