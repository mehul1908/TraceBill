package com.tracebill.module.invoice.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.tracebill.module.invoice.domain.InvoiceAggregate;
import com.tracebill.module.invoice.domain.PartyHierarchyValidator;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.enums.InvoiceStatus;
import com.tracebill.module.party.entity.Party;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceDomainService {

    private final PartyHierarchyValidator hierarchyValidator;

    public void validateHierarchy(Party seller, Party buyer) {
        hierarchyValidator.validate(seller, buyer);
    }

    public InvoiceAggregate createEmptyInvoice(
            String invoiceNo,
            LocalDate date,
            Long fromBillingEntityId,
            Long toBillingEntityId
    ) {
        Invoice invoice = Invoice.builder()
                .invoiceNo(invoiceNo)
                .fromBillingEntityId(fromBillingEntityId)
                .toBillingEntityId(toBillingEntityId)
                .date(date)
                .status(InvoiceStatus.SAVED)
                .build();

        return new InvoiceAggregate(invoice);
    }
}


