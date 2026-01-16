package com.tracebill.module.invoice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.module.invoice.dto.InvoiceRegisterModel;

import jakarta.transaction.Transactional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	private InvoiceApplicationService invoiceApplicationService;
	
	@Override
	@Transactional
	public String generateInvoice(InvoiceRegisterModel model) {
		
		return invoiceApplicationService.generateInvoice(model);
	}

}
