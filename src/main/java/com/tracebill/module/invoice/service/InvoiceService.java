package com.tracebill.module.invoice.service;

import com.tracebill.module.invoice.dto.InvoiceRegisterModel;

public interface InvoiceService {

	String generateInvoice(InvoiceRegisterModel model);

}
