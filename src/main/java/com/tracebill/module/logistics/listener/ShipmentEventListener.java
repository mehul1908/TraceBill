package com.tracebill.module.logistics.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.enums.BlockchainIntentType;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.blockchain.service.BlockchainService;
import com.tracebill.module.logistics.entity.Shipment;
import com.tracebill.module.logistics.entity.ShipmentItem;
import com.tracebill.module.logistics.enums.ShipmentStatus;
import com.tracebill.module.logistics.record.ShipmentDispatchedEvent;
import com.tracebill.module.logistics.repo.ShipmentRepo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ShipmentEventListener {

    @Autowired
    private ShipmentRepo shipmentRepo;
    
    @Autowired
    private BlockchainService blockchainService;
    
    @Autowired
    private BlockchainIntentRepo intentRepo;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleShipmentDispatch(ShipmentDispatchedEvent event) {

        Shipment shipment = shipmentRepo.findById(event.shipmentId())
                .orElseThrow();
        log.info("📦 Shipment dispatched event received id={}", event.shipmentId());

        BlockchainIntent intent = BlockchainIntent.builder()
                .intentType(BlockchainIntentType.SHIPMENT_DISPATCH)
                .referenceType("SHIPMENT")
                .referenceId(shipment.getShipmentId())
                .dataHash(shipment.computeHash()) // canonical hash
                .fromPartyId(shipment.getFromPartyId())
                .toPartyId(shipment.getToPartyId())
                .status(BlockchainIntentStatus.PENDING)
                .build();

        intentRepo.save(intent);

        // DO NOT execute blockchain txs here
        // Let BlockchainIntentProcessor pick it up

        shipment.setStatus(ShipmentStatus.BLOCKCHAIN_PENDING);
        shipmentRepo.save(shipment);
    }

}