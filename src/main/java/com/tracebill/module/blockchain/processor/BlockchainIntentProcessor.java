package com.tracebill.module.blockchain.processor;

import org.springframework.stereotype.Component;

import com.tracebill.module.batch.service.BatchService;
import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.blockchain.service.BlockchainService;
import com.tracebill.module.logistics.entity.Shipment;
import com.tracebill.module.logistics.entity.ShipmentItem;
import com.tracebill.module.logistics.repo.ShipmentRepo;
import com.tracebill.module.logistics.service.ShipmentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BlockchainIntentProcessor {
	
	private final ShipmentService shipmentService;
	private final BlockchainService blockchainService;
	private final BlockchainIntentRepo intentRepo;
	private final BatchService batchService;

    private final ShipmentBlockchainExecutor shipmentExecutor;
    private final ProductBlockchainExecutor productExecutor;

    @Transactional
    public void process(BlockchainIntent intent) {

        switch (intent.getIntentType()) {
            case SHIPMENT_DISPATCH ->
                    shipmentExecutor.processShipmentDispatch(intent);
            case PRODUCT_REGISTER ->
            			productExecutor.processProductCreation(intent);
            
            default ->
                    throw new IllegalStateException(
                        "Unsupported intent type: " + intent.getIntentType()
                    );
        }
    }
}

