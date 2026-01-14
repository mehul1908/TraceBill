package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.inventory.entity.InventoryTransaction;
import com.tracebill.module.inventory.repo.InventoryTxnRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryTxnServiceImpl implements InventoryTxnService {

    private final InventoryTxnRepo txnRepo;
    private final AuthenticatedUserProvider authenticatedUser;

    @Override
    public void recordInvoiceConsumption(
            String invoiceNo,
            Long productId,
            List<BatchQuantityDTO> batches
    ) {

        Long ownerId = authenticatedUser.getAuthenticatedParty();

        for (BatchQuantityDTO dto : batches) {

            InventoryTransaction txn = InventoryTransaction.builder()
                    .refType("INVOICE")
                    .refNo(invoiceNo)
                    .productId(productId)
                    .batchId(dto.getBatchId())
                    .qty(dto.getQuantity().negate())
                    .performedOn(ownerId)
                    .build();

            txnRepo.save(txn);
        }
    }

    @Override
    public void recordProduction(
            Long batchId,
            Long productId,
            BigInteger qty
    ) {
    	Long partyId = authenticatedUser.getAuthenticatedParty();

        InventoryTransaction txn = InventoryTransaction.builder()
                .refType("PRODUCTION")
                .refNo(batchId.toString())
                .productId(productId)
                .batchId(batchId)
                .qty(qty)
                .performedOn(partyId)
                .build();

        txnRepo.save(txn);
    }
}
