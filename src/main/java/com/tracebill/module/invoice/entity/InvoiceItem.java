package com.tracebill.module.invoice.entity;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_item" ,
	indexes = {
			@Index(name = "idx_invoice_id" , columnList = "invoice_invoice_id")
	}
		)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long invoiceItemId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Invoice invoice;
	
	@Column(nullable = false)
	private Long productId;
	
	@Column(nullable = false)
	private Long batchId;
	
	@Column(nullable = false)
	private BigInteger qty;
	
	@Column(nullable = false)
	private BigDecimal rate;
	
	@Builder.Default
	private BigDecimal disc = new BigDecimal(0);
	
}
