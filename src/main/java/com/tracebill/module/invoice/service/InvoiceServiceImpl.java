package com.tracebill.module.invoice.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.module.audit.service.AuditLogService;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.inventory.service.BatchInvService;
import com.tracebill.module.inventory.service.ProductInvService;
import com.tracebill.module.invoice.dto.InvoiceItemRegisterModel;
import com.tracebill.module.invoice.dto.InvoiceRegisterModel;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.invoice.enums.InvoiceStatus;
import com.tracebill.module.invoice.repo.InvoiceItemRepo;
import com.tracebill.module.invoice.repo.InvoiceRepo;
import com.tracebill.module.party.entity.BillingEntity;
import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.enums.PartyType;
import com.tracebill.module.party.service.BillingEntityService;
import com.tracebill.module.party.service.PartyService;
import com.tracebill.module.production.entity.Product;
import com.tracebill.module.production.service.ProductService;
import com.tracebill.util.SequenceGeneratorService;

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
