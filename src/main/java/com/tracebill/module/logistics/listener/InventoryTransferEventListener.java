package com.tracebill.module.logistics.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.enums.BlockchainIntentType;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.blockchain.service.BlockchainService;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.logistics.record.InventoryTransferEvent;
import com.tracebill.module.logistics.repo.ShipmentRepo;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class InventoryTransferEventListener {
    
    @Autowired
    private BlockchainIntentRepo intentRepo;
	
	@Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInventoryTransfer(InventoryTransferEvent event) {

    		for(Invoice invoice : event.invoices()) {
    			for(InvoiceItem item : invoice.getItems()) {
    				log.info(item.getInvoiceItemId().toString());
    				BlockchainIntent intent = BlockchainIntent.builder()
    		                .intentType(BlockchainIntentType.INVENTORY_TRANSFER)
    		                .referenceType("INVOICE_ITEM")
    		                .referenceId(item.getInvoiceItemId())
    		                .dataHash(item.computeHash()) // canonical hash
    		                .fromPartyId(invoice.getFromBillingEntityId())
    		                .toPartyId(invoice.getToBillingEntityId())
    		                .status(BlockchainIntentStatus.PENDING)
    		                .build();

    		        intentRepo.save(intent);
    			}
    			
    		}
    		
    		
    		
    }
	
}
