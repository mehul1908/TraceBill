package com.tracebill.module.invoice.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import com.tracebill.module.production.service.ProductService;
import com.tracebill.util.SequenceGeneratorService;

import jakarta.transaction.Transactional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	private InvoiceRepo invoiceRepo;
	
	@Autowired
	private InvoiceItemRepo invoiceItemRepo;
	
	@Autowired
	private AuthenticatedUserProvider authenticatedUser;
	
	@Autowired
	private AuditLogService auditService;
	
	@Autowired
	private PartyService partyService;
	
	@Autowired
	private BillingEntityService billingEntityService;
	
	@Autowired
	private SequenceGeneratorService sequenceGenerator;
	
	@Autowired
	private BatchInvService batchInvService;
	
	@Autowired
	private ProductService productService;
	
	@Override
	@Transactional
	public String generateInvoice(InvoiceRegisterModel model) {
		
		LocalDate date = model.getDate()==null?LocalDate.now():model.getDate();
		
		Long toPartyId = authenticatedUser.getAuthenticatedParty();
		Long fromPartyId = model.getFromPartyId();
		
		Party toParty = partyService.getPartyById(toPartyId);
		Party fromParty = partyService.getPartyById(fromPartyId);
		
		BillingEntity toBillingEntity =  billingEntityService.getBillingEntityByPartyId(toPartyId);
		BillingEntity fromBillingEntity =  billingEntityService.getBillingEntityByPartyId(fromPartyId);
		
		Boolean flag1 = (toParty.getType()==PartyType.MANUFACTURER && fromParty.getType()==PartyType.WAREHOUSE) ||
				(toParty.getType()==PartyType.WAREHOUSE && fromParty.getType()==PartyType.WHOLESALER) ||
				(toParty.getType()==PartyType.WHOLESALER && fromParty.getType()==PartyType.RETAILER);
		
		Boolean flag2 = fromParty.getParentPartyId() == toPartyId;
		
		if(!(flag1 && flag2)) {
			throw new IllegalStateException("Hierarchy is not matched");
		}
		
		String invoiceNo = sequenceGenerator.generateInvoiceNo(
			    fromPartyId,
			    fromParty.getPartyCode(),
			    date
			);
		
		Invoice invoice = Invoice.builder()
				.invoiceNo(invoiceNo)
				.toBillingEntityId(toBillingEntity.getBillingEntityId())
				.fromBillingEntityId(fromBillingEntity.getBillingEntityId())
				.amt(BigDecimal.valueOf(0))
				.tax(BigDecimal.valueOf(0))
				.totalAmt(BigDecimal.valueOf(0))
				.date(date)
				.status(InvoiceStatus.SAVED)
				.build();
		
		BigDecimal amt = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal totalAmt = BigDecimal.ZERO;
		List<InvoiceItem> invoiceItems = new ArrayList<>();
		List<InvoiceItemRegisterModel> items = model.getItems();
		
		for(InvoiceItemRegisterModel item : items) {
			if(!productService.existById(item.getProductId())) {
				throw new ResourceNotFoundException("Product with given id not found :" + item.getProductId());
			}
			
			BigDecimal disc = item.getDisc() == null ? BigDecimal.ZERO : item.getDisc();
			
			List<BatchQuantityDTO> dtos = batchInvService.getBatchAndQuantityByProductAndQuantity(item.getProductId() , item.getQty());
			for(BatchQuantityDTO dto : dtos) {
				InvoiceItem invoiceItem = InvoiceItem.builder()
						.invoice(invoice)
						.productId(item.getProductId())
						.batchId(dto.getBatchId())
						.qty(dto.getQuantity())
						.rate(item.getRate())
						.disc(disc)
						.build();
			}
			
			BigDecimal itemAmt =
			        item.getRate().multiply(new BigDecimal(item.getQty()));

			
		}
	}

}
