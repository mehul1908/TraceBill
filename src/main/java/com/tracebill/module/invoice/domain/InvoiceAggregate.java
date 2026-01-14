package com.tracebill.module.invoice.domain;

import java.util.ArrayList;
import java.util.List;

import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;

public class InvoiceAggregate {

    private final Invoice invoice;
    private final List<InvoiceItem> items = new ArrayList<>();

    public InvoiceAggregate(Invoice invoice) {
        this.invoice = invoice;
    }

    public void addItems(List<InvoiceItem> newItems) {
        items.addAll(newItems);
    }

    public Invoice getInvoice() {
        invoice.setItems(items);
        return invoice;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }
}
