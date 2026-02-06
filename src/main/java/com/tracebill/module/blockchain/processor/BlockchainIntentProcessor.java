package com.tracebill.module.blockchain.processor;

import org.springframework.stereotype.Component;

import com.tracebill.module.blockchain.entity.BlockchainIntent;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BlockchainIntentProcessor {
	
	private final BatchBlockchainExecutor batchExecutor;
    private final ShipmentBlockchainExecutor shipmentExecutor;
    private final ProductBlockchainExecutor productExecutor;
    private final InvoiceBlockchainExecutor invoiceExecutor;

    @Transactional
    public void process(BlockchainIntent intent) {

        switch (intent.getIntentType()) {
            case SHIPMENT_DISPATCH ->
                    shipmentExecutor.processShipmentDispatch(intent);
            case PRODUCT_REGISTER ->
            			productExecutor.processProductCreation(intent);
            case BATCH_CREATE ->
            			batchExecutor.processBatchCreation(intent);
            case INVENTORY_TRANSFER ->
            			batchExecutor.processInventoryTransfer(intent);
            case INVOICE_ANCHOR ->
            			invoiceExecutor.processInventoryTransfer(intent);
            default ->
                    throw new IllegalStateException(
                        "Unsupported intent type: " + intent.getIntentType()
                    );
        }
    }
}

