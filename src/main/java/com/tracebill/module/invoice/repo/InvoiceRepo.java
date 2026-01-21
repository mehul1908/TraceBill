package com.tracebill.module.invoice.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tracebill.module.invoice.entity.Invoice;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, Long> {

	@Query("""
			    select i
			    from Invoice i
			    left join fetch i.items
			    where i.invoiceId = :invoiceId
			""")
	Optional<Invoice> findByIdWithItems(Long invoiceId);
}
