package com.tracebill.module.invoice.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.inventory.service.BatchInvService;
import com.tracebill.module.inventory.service.InventoryApplicationService;
import com.tracebill.module.inventory.service.ProductInvService;
import com.tracebill.module.invoice.domain.InvoiceAggregate;
import com.tracebill.module.invoice.domain.InvoiceCalculator;
import com.tracebill.module.invoice.dto.InvoiceItemRegisterModel;
import com.tracebill.module.invoice.dto.InvoiceRegisterModel;
import com.tracebill.module.invoice.entity.Invoice;
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
@Transactional
public class InvoiceApplicationService {

    private final InvoiceRepo invoiceRepo;
    private final PartyService partyService;
    private final BillingEntityService billingEntityService;
    private final InvoiceDomainService invoiceDomainService;
    private final AuthenticatedUserProvider authUser;
    private final SequenceGeneratorService sequenceGenerator;
    private final BatchInvService batchInvService;
    private final ProductInvService productInvService;
    private final ProductService productService;
    private final InvoiceCalculator invoiceCalculator;
    private final InventoryApplicationService inventoryApplicationService;
    

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

        Invoice saved = invoiceRepo.save(aggregate.getInvoice());

        // inventory mutation AFTER invoice persistence
        inventoryApplicationService.consumeForInvoice(
                saved.getInvoiceNo(),
                model.getItems()
        );

        return saved.getInvoiceNo();
    }

}
