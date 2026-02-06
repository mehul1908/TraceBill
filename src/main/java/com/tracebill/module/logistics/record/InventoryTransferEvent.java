package com.tracebill.module.logistics.record;

import java.util.List;

import com.tracebill.module.invoice.entity.Invoice;

public record InventoryTransferEvent(List<Invoice> invoices) {

}
