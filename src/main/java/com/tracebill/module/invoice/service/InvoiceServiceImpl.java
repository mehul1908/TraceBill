package com.tracebill.module.invoice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.exception.UnauthorizedUserException;
import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.audit.service.AuditLogService;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.inventory.service.BatchInvService;
import com.tracebill.module.inventory.service.InventoryApplicationService;
import com.tracebill.module.invoice.domain.InvoiceAggregate;
import com.tracebill.module.invoice.domain.InvoiceCalculator;
import com.tracebill.module.invoice.dto.InvoiceItemRegisterModel;
import com.tracebill.module.invoice.dto.InvoiceRegisterModel;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.invoice.enums.InvoiceStatus;
import com.tracebill.module.invoice.repo.InvoiceItemRepo;
import com.tracebill.module.invoice.repo.InvoiceRepo;
import com.tracebill.module.party.entity.BillingEntity;
import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.service.BillingEntityService;
import com.tracebill.module.party.service.PartyService;
import com.tracebill.module.production.entity.Product;
import com.tracebill.module.production.service.ProductService;
import com.tracebill.util.SequenceGeneratorService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepo invoiceRepo;
    private final PartyService partyService;
    private final BillingEntityService billingEntityService;
    private final InvoiceDomainService invoiceDomainService;
    private final AuthenticatedUserProvider authUser;
    private final SequenceGeneratorService sequenceGenerator;
    private final BatchInvService batchInvService;
    private final ProductService productService;
    private final InvoiceCalculator invoiceCalculator;
    private final InventoryApplicationService inventoryApplicationService;
    private final InvoiceItemRepo invoiceItemRepo;
    private final AuditLogService auditService;

	@Override
	@Transactional
	public String generateInvoice(InvoiceRegisterModel model) {

		LocalDate invoiceDate = model.getDate() != null
                ? model.getDate()
                : LocalDate.now();

        Long sellerPartyId = authUser.getAuthenticatedParty();
        Long buyerPartyId = model.getToPartyId();

        Party seller = partyService.getPartyById(sellerPartyId);
        Party buyer = partyService.getPartyById(buyerPartyId);
        
        BillingEntity sellerBE = billingEntityService.getBillingEntityByPartyId(sellerPartyId);
        BillingEntity buyerBE = billingEntityService.getBillingEntityByPartyId(buyerPartyId);

        invoiceDomainService.validateHierarchy(seller, buyer);

        String invoiceNo = sequenceGenerator.generateInvoiceNo(
                sellerPartyId,
                seller.getPartyCode(),
                invoiceDate
        );

        InvoiceAggregate aggregate = invoiceDomainService.createEmptyInvoice(
                invoiceNo,
                invoiceDate,
                sellerBE.getBillingEntityId(),
                buyerBE.getBillingEntityId()
        );

        for (InvoiceItemRegisterModel item : model.getItems()) {

            Product product = productService.getProductById(item.getProductId());

            List<BatchQuantityDTO> allocations =
                    batchInvService.allocateFIFO(
                            item.getProductId(),
                            item.getQty()
                    );

            aggregate.addItems(
                    invoiceCalculator.buildInvoiceItems(
                            aggregate.getInvoice(),
                            product,
                            item,
                            allocations
                    )
            );
        }

        invoiceCalculator.calculateTotals(aggregate);

        Invoice savedInvoice = invoiceRepo.save(aggregate.getInvoice());

        for (InvoiceItem item : aggregate.getItems()) {
            item.setInvoice(savedInvoice); // owning side
            invoiceItemRepo.save(item);
        }


        // inventory mutation AFTER invoice persistence
        inventoryApplicationService.consumeForInvoice(
                savedInvoice.getInvoiceNo(),
                model.getItems()
        );
        auditService.create(AuditAction.CREATED, "Invoice Created : " + savedInvoice.getInvoiceNo());
        return savedInvoice.getInvoiceNo();
	}

	@Override
	public Invoice getInvoiceById(Long invoiceId) {
		return invoiceRepo.findById(invoiceId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice with given id not found" + invoiceId));
	}

	@Override
	public Invoice getInvoiceWithItems(Long invoiceId) {
		return invoiceRepo.findByIdWithItems(invoiceId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice with given id not found" + invoiceId));
	}

	@Override
	@Transactional
	public void addShipment(List<Invoice> invoices, Long shipmentId) {

		for (Invoice invoice : invoices) {

			if (invoice.getStatus() != InvoiceStatus.SAVED) {
				throw new IllegalStateException("Invoice not eligible for shipment: " + invoice.getInvoiceNo());
			}

			invoice.setShipmentId(shipmentId);
			invoice.setStatus(InvoiceStatus.VEHICLE_ALLOTED);
		}
		invoiceRepo.saveAll(invoices);

	}

	@Override
	public List<Invoice> getInvoiceByShipmentId(Long shipmentId) {
		return invoiceRepo.findByShipmentId(shipmentId);

	}

	@Override
	@Transactional
	public void markDispatched(List<Invoice> invoices) {
		for (Invoice invoice : invoices) {
			if (invoice.getStatus() != InvoiceStatus.VEHICLE_ALLOTED) {
				throw new IllegalStateException("Invoice not eligible for dispatched: " + invoice.getInvoiceNo());
			}

			invoice.setStatus(InvoiceStatus.DISPATCHED);
		}
		invoiceRepo.saveAll(invoices);

	}

	@Override
	@Transactional
	public void markReceived(List<Invoice> invoices) {
		for (Invoice invoice : invoices) {
			if (invoice.getStatus() != InvoiceStatus.DISPATCHED) {
				throw new IllegalStateException("Invoice not eligible for received: " + invoice.getInvoiceNo());
			}

			invoice.setStatus(InvoiceStatus.RECEIVED);
		}
		invoiceRepo.saveAll(invoices);
	}

	@Override
	@Transactional
	public void markSaved(List<Invoice> invoices) {
		for (Invoice invoice : invoices) {
			if (invoice.getStatus() != InvoiceStatus.VEHICLE_ALLOTED) {
				throw new IllegalStateException("Invoice not eligible for cancel shipment: " + invoice.getInvoiceNo());
			}

			invoice.setStatus(InvoiceStatus.SAVED);
		}
		invoiceRepo.saveAll(invoices);
	}

	@Override
	@Transactional
	public void cancelInvoice(Long invoiceId) {
		
		Long partyId = authUser.getAuthenticatedParty();
		Invoice invoice = this.getInvoiceById(invoiceId);
		
		BillingEntity billingEntity = billingEntityService.getBillingEntityByPartyId(partyId);
		
		if(!Objects.equals(invoice.getFromBillingEntityId(), billingEntity.getBillingEntityId())) {
			throw new UnauthorizedUserException("You are not authorized to cancel it");
		}
		if (invoice.getStatus() != InvoiceStatus.SAVED ) {
			throw new IllegalStateException("Invoice not eligible for cancel: " + invoice.getInvoiceNo());
		}

		List<InvoiceItem> items = invoice.getItems();
		inventoryApplicationService.rollbackInvoice(items);
		
		invoice.setStatus(InvoiceStatus.CANCELLED);
		
		invoiceRepo.save(invoice);
		
		auditService.create(AuditAction.STATUS_CHANGED, "Invoice Cancelled : " + invoice.getInvoiceNo());
		
	}

}
