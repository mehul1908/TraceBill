package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import java.util.List;

import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.logistics.entity.ShipmentItem;

public interface BatchInvService {

    Long createBatchInventory(
            Long batchId,
            Long prodInvId,
            BigInteger manufacturedQty
    );

    List<BatchQuantityDTO> allocateFIFO(
            Long productId,
            BigInteger requiredQty
    );

    void consumeBatches(
            List<BatchQuantityDTO> allocations
    );

	void addBatches(Long batchId, BigInteger qty);
}

