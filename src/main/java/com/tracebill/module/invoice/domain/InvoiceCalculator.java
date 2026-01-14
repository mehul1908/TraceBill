package com.tracebill.module.invoice.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tracebill.module.inventory.dto.BatchQuantityDTO;
import com.tracebill.module.invoice.dto.InvoiceItemRegisterModel;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.production.entity.Product;

@Component
public class InvoiceCalculator {

    public List<InvoiceItem> buildInvoiceItems(
            Invoice invoice,
            Product product,
            InvoiceItemRegisterModel item,
            List<BatchQuantityDTO> allocations
    ) {

        BigDecimal disc = item.getDisc() != null
                ? item.getDisc()
                : BigDecimal.ZERO;

        BigDecimal taxRate = product.getCessRate().add(product.getGstRate());
        
        return allocations.stream()
                .map(dto -> InvoiceItem.builder()
                        .invoice(invoice)
                        .productId(item.getProductId())
                        .batchId(dto.getBatchId())
                        .qty(dto.getQuantity())
                        .rate(item.getRate())
                        .disc(disc)
                        .taxRate(taxRate)
                        .build()
                )
                .toList();
    }

    public void calculateTotals(InvoiceAggregate aggregate) {

        BigDecimal amt = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;

        for (InvoiceItem item : aggregate.getItems()) {

            BigDecimal qty = new BigDecimal(item.getQty());
            BigDecimal gross = item.getRate().multiply(qty);
            BigDecimal net = gross.subtract(item.getDisc());

            BigDecimal taxRate = item.getTaxRate(); // derived from product
            BigDecimal itemTax = net.multiply(taxRate);

            amt = amt.add(net);
            tax = tax.add(itemTax);
        }

        aggregate.getInvoice().setAmt(amt);
        aggregate.getInvoice().setTax(tax);
        aggregate.getInvoice().setTotalAmt(amt.add(tax));
    }
}
