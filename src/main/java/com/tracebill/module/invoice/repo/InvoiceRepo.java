package com.tracebill.module.invoice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, Long>{

}
