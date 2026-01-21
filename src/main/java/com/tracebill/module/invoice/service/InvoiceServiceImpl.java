package com.tracebill.module.invoice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.module.invoice.dto.InvoiceRegisterModel;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.enums.InvoiceStatus;
import com.tracebill.module.invoice.repo.InvoiceRepo;

import jakarta.transaction.Transactional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	private InvoiceApplicationService invoiceApplicationService;
	
	@Autowired
	private InvoiceRepo invoiceRepo;
	
	@Override
	@Transactional
	public String generateInvoice(InvoiceRegisterModel model) {
		
		return invoiceApplicationService.generateInvoice(model);
	}

	@Override
	public Invoice getInvoiceById(Long invoiceId) {
		return invoiceRepo.findById(invoiceId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice with given id not found"  + invoiceId));
	}

	@Override
	public Invoice getInvoiceWithItems(Long invoiceId) {
		return invoiceRepo.findByIdWithItems(invoiceId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice with given id not found"  + invoiceId));
	}

	@Override
	public void addShipment(List<Invoice> invoices, Long shipmentId) {
		
		for(Invoice invoice : invoices) {
			
			if(invoice.getStatus() != InvoiceStatus.SAVED) {
				throw new IllegalStateException(
				        "Invoice not eligible for shipment: " + invoice.getInvoiceNo()
				    );
			}
			
			invoice.setShipmentId(shipmentId);
			invoice.setStatus(InvoiceStatus.VEHICLE_ALLOTED);
		}
		invoiceRepo.saveAll(invoices);
		
	}

}
