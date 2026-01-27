package com.tracebill.module.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.invoice.dto.InvoiceItemRegisterModel;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.logistics.entity.ShipmentItem;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryApplicationService {
	
	private final BatchInvService batchInvService;
	private final ProductInvService productInvService;
	private final InventoryTxnService inventoryTxnService;
	private final AuthenticatedUserProvider authenticatedUser;

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

	@Transactional
	public void addFromInvoice(List<ShipmentItem> items) {
		Long ownerId = authenticatedUser.getAuthenticatedParty();
		for(ShipmentItem shipmentItem : items) {
			batchInvService.addBatches(shipmentItem.getBatchId() , shipmentItem.getQty());
			Long productInv = productInvService.getProdInvByProdAndPartyOrCreate(shipmentItem.getProductId(), ownerId);
			productInvService.addQuantity(productInv, shipmentItem.getQty());
		}
	}

	public void rollbackInvoice(List<InvoiceItem> items) {
		Long ownerId = authenticatedUser.getAuthenticatedParty();
		for(InvoiceItem invoiceItem : items) {
			batchInvService.addBatches(invoiceItem.getBatchId() , invoiceItem.getQty());
			Long productInv = productInvService.getProdInvByProdAndPartyOrCreate(invoiceItem.getProductId(), ownerId);
			productInvService.addQuantity(productInv, invoiceItem.getQty());
		}
	}

	
}
