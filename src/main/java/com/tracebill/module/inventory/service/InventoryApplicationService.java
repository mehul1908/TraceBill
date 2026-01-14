package com.tracebill.module.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.invoice.dto.InvoiceItemRegisterModel;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryApplicationService {
	
	private final BatchInvService batchInvService;
	private final ProductInvService productInvService;
	private final InventoryTxnService inventoryTxnService;

	public void consumeForInvoice(
	        String invoiceNo,
	        List<InvoiceItemRegisterModel> items
	) {
	    for (InvoiceItemRegisterModel item : items) {
	        List<BatchQuantityDTO> allocations =
	                batchInvService.allocateFIFO(item.getProductId(), item.getQty());

	        batchInvService.consumeBatches(allocations);
	        productInvService.subtractQuantity(item.getProductId(), item.getQty());
	        inventoryTxnService.recordInvoiceConsumption(
	                invoiceNo,
	                item.getProductId(),
	                allocations
	        );
	    }
	}

	
}
