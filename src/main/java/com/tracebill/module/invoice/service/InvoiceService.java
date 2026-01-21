package com.tracebill.module.invoice.service;

import java.util.List;

import com.tracebill.module.invoice.dto.InvoiceRegisterModel;
import com.tracebill.module.invoice.entity.Invoice;

public interface InvoiceService {

	String generateInvoice(InvoiceRegisterModel model);

	Invoice getInvoiceById(Long invoiceId);

	Invoice getInvoiceWithItems(Long invoiceId);

	void addShipment(List<Invoice> invoices, Long shipmentId);

}
