package com.tracebill.module.invoice.service;

import java.util.List;

import com.tracebill.module.invoice.dto.InvoiceRegisterModel;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;

public interface InvoiceService {

	String generateInvoice(InvoiceRegisterModel model);

	Invoice getInvoiceById(Long invoiceId);

	Invoice getInvoiceWithItems(Long invoiceId);

	void addShipment(List<Invoice> invoices, Long shipmentId);

	List<Invoice> getInvoiceByShipmentId(Long shipmentId);

	void markDispatched(List<Invoice> invoices);

	void markReceived(List<Invoice> invoices);

	void markSaved(List<Invoice> invoices);

	void cancelInvoice(Long invoiceId);

	InvoiceItem getInvoiceItemByInvoiceItemId(Long referenceId);
	
	List<Invoice> getInvoiceByShipmentWithItems(Long shipmentId);

}
