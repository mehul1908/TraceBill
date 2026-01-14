package com.tracebill.module.inventory.service;

import java.math.BigInteger;
import java.util.List;

import com.tracebill.module.inventory.dto.BatchQuantityDTO;

public interface InventoryTxnService {

    void recordInvoiceConsumption(
            String invoiceNo,
            Long productId,
            List<BatchQuantityDTO> batches
    );

    void recordProduction(
            Long batchId,
            Long productId,
            BigInteger qty
    );
}
