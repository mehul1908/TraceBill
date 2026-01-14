package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.inventory.entity.BatchInventory;
import com.tracebill.module.inventory.entity.ProductInventory;
import com.tracebill.module.inventory.exception.InsufficientStockException;
import com.tracebill.module.inventory.repo.BatchInvRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchInvServiceImpl implements BatchInvService {

    private final AuthenticatedUserProvider authenticatedUser;
    private final BatchInvRepo batchInvRepo;
    private final ProductInvService productInvService;

    @Override
    @Transactional
    public Long createBatchInventory(
            Long batchId,
            Long prodInvId,
            BigInteger manufacturedQty
    ) {

        Long ownerId = authenticatedUser.getAuthenticatedParty();

        BatchInventory batchInv = BatchInventory.builder()
                .batchId(batchId)
                .qty(manufacturedQty)
                .ownerId(ownerId)
                .build();

        BatchInventory saved = batchInvRepo.save(batchInv);

        productInvService.addQuantity(prodInvId, manufacturedQty);

        return saved.getBatchInvId();
    }

    @Override
    public List<BatchQuantityDTO> allocateFIFO(
            Long productId,
            BigInteger requiredQty
    ) {

        Long ownerId = authenticatedUser.getAuthenticatedParty();

        ProductInventory productInv =
                productInvService.getProdInvByProdAndParty(productId, ownerId);

        if (productInv.getQty().compareTo(requiredQty) < 0) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + productId
            );
        }

        List<BatchInventory> batches =
                batchInvRepo.findAvailableBatchesFIFO(productId, ownerId);

        if (batches.isEmpty()) {
            throw new InsufficientStockException(
                    "No batch stock available for product: " + productId
            );
        }

        BigInteger remaining = requiredQty;
        List<BatchQuantityDTO> result = new ArrayList<>();

        for (BatchInventory batch : batches) {

            if (remaining.signum() <= 0) break;

            BigInteger takeQty = batch.getQty().min(remaining);

            result.add(new BatchQuantityDTO(batch.getBatchId(), takeQty));
            remaining = remaining.subtract(takeQty);
        }

        if (remaining.signum() > 0) {
            throw new InsufficientStockException("FIFO allocation failed");
        }

        return result;
    }

    @Override
    @Transactional
    public void consumeBatches(
            List<BatchQuantityDTO> allocations
    ) {

        for (BatchQuantityDTO dto : allocations) {

            BatchInventory batch =
                    batchInvRepo.findByBatchId(dto.getBatchId())
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Batch not found: " + dto.getBatchId()
                                    )
                            );

            batch.subtractStock(dto.getQuantity());
            batchInvRepo.save(batch);
        }
    }
}
