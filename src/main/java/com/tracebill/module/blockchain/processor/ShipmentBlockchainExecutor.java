package com.tracebill.module.blockchain.processor;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.tracebill.module.batch.service.BatchService;
import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.entity.BlockchainTransaction;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.repo.BlockchainIntentRepo;
import com.tracebill.module.blockchain.repo.BlockchainTxnRepo;
import com.tracebill.module.blockchain.service.BlockchainService;
import com.tracebill.module.logistics.entity.Shipment;
import com.tracebill.module.logistics.entity.ShipmentItem;
import com.tracebill.module.logistics.enums.ShipmentStatus;
import com.tracebill.module.logistics.service.ShipmentService;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShipmentBlockchainExecutor {

    private final ShipmentService shipmentService;
    private final BlockchainService blockchainService;
    private final BlockchainIntentRepo intentRepo;
    private final BatchService batchService;
    private final BlockchainTxnRepo blockchainTransactionRepo;

    @Transactional
    public void processShipmentDispatch(BlockchainIntent intent) {

        Shipment shipment =
                shipmentService.findShipmentById(intent.getReferenceId());
        List<String> txHashes = new ArrayList<>();

        for (ShipmentItem item : shipment.getItems()) {

            String batchHash =
                    batchService.getBatchHashById(item.getBatchId());

            TransactionReceipt receipt =
                    blockchainService.transferBatch(
                            batchHash,
                            shipment.getToPartyId().toString(),
                            item.getQty().intValueExact()
                    );
            
            BlockchainTransaction tx = BlockchainTransaction.builder()
                    .intentId(intent.getId())
                    .txHash(receipt.getTransactionHash().toLowerCase())
                    .blockNumber(receipt.getBlockNumber().longValue())
                    .contractAddress(receipt.getTo())
                    .submittedWallet(receipt.getFrom())
                    .eventName("BATCH_TRANSFER")
                    .submittedAt(LocalDateTime.now())
                    .build();

            blockchainTransactionRepo.save(tx);
            txHashes.add(receipt.getBlockHash());
            intent.setDataHash(String.join(",", txHashes));
        }
        log.info("⛓️ Executing shipment blockchain intent id={}", intent.getId());
        intent.setStatus(BlockchainIntentStatus.SUBMITTED);
        intentRepo.save(intent);
        shipment.setStatus(ShipmentStatus.DISPATCHED);
        shipmentService.save(shipment);
    }
}
