package com.tracebill.module.blockchain.processor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.tracebill.module.batch.entity.Batch;
import com.tracebill.module.batch.service.BatchService;
import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.entity.BlockchainTransaction;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.blockchain.repo.BlockchainTxnRepo;
import com.tracebill.module.blockchain.service.BlockchainService;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.invoice.service.InvoiceService;
import com.tracebill.module.production.entity.Product;
import com.tracebill.module.production.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchBlockchainExecutor {

	private final BatchService batchService;
	private final BlockchainService blockchainService;
	private final BlockchainIntentRepo intentRepo;
    private final BlockchainTxnRepo blockchainTransactionRepo;
    private final ProductService productService;
    private final InvoiceService invoiceService;
    
    
    @Transactional
    public void processBatchCreation(BlockchainIntent intent) {
    	
    		Batch batch = batchService.getBatchByBatchId(intent.getReferenceId());
    		Product product = productService.getProductById(batch.getProductId());
    		TransactionReceipt receipt = blockchainService.batchCreation(batch.getBatchHash(), product.getProductHash(), batch.getManufacturedQty());
    		BlockchainTransaction tx = BlockchainTransaction.builder()
                    .intentId(intent.getId())
                    .txHash(receipt.getTransactionHash().toLowerCase())
                    .blockNumber(receipt.getBlockNumber().longValue())
                    .contractAddress(receipt.getTo())
                    .submittedWallet(receipt.getFrom())
                    .eventName("BATCH_REGISTER")
                    .submittedAt(LocalDateTime.now())
                    .build();
    		blockchainTransactionRepo.save(tx);
    		intent.setDataHash(product.getProductHash());
    		intent.setStatus(BlockchainIntentStatus.SUBMITTED);
    		intentRepo.save(intent);
    }
	
    @Transactional
    public void processInventoryTransfer(BlockchainIntent intent) {
    	
    		InvoiceItem item = invoiceService.getInvoiceItemByInvoiceItemId(intent.getReferenceId());
    		Batch batch = batchService.getBatchByBatchId(item.getBatchId());
    		Product product = productService.getProductById(batch.getProductId());
    		TransactionReceipt receipt = blockchainService.batchCreation(batch.getBatchHash(), product.getProductHash(), item.getQty());
    		BlockchainTransaction tx = BlockchainTransaction.builder()
                    .intentId(intent.getId())
                    .txHash(receipt.getTransactionHash().toLowerCase())
                    .blockNumber(receipt.getBlockNumber().longValue())
                    .contractAddress(receipt.getTo())
                    .submittedWallet(receipt.getFrom())
                    .eventName("INVENTORY_TRANSFER")
                    .submittedAt(LocalDateTime.now())
                    .build();
    		blockchainTransactionRepo.save(tx);
    		intent.setDataHash(product.getProductHash());
    		intent.setStatus(BlockchainIntentStatus.SUBMITTED);
    		intentRepo.save(intent);
    }
    
    
    
}
