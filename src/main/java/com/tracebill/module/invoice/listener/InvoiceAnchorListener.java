package com.tracebill.module.invoice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.enums.BlockchainIntentType;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.record.InvoiceAnchorEvent;
import com.tracebill.module.invoice.service.InvoiceService;
import com.tracebill.module.logistics.listener.InventoryTransferEventListener;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InvoiceAnchorListener {

	@Autowired
    private BlockchainIntentRepo intentRepo;
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvoiceAnchor(InvoiceAnchorEvent event) {
		Invoice invoice = invoiceService.getInvoiceById(event.invoiceId());
		BlockchainIntent intent = BlockchainIntent.builder()
                .intentType(BlockchainIntentType.INVOICE_ANCHOR)
                .referenceType("INVOICE")
                .referenceId(invoice.getInvoiceId())
                .dataHash(invoice.computeHash()) // canonical hash
                .fromPartyId(invoice.getFromBillingEntityId())
                .toPartyId(invoice.getToBillingEntityId())
                .status(BlockchainIntentStatus.PENDING)
                .build();

        intentRepo.save(intent);
	}
}
