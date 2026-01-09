package com.tracebill.module.invoice.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice",
indexes = {
		@Index(name = "index_invoice_no" , columnList = "invoice_no")
}
	)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long invoiceId;
	
	@Column(nullable = false , unique = true)
	private String invoiceNo;
	
	@Column(nullable = false)
	private Long toBillingEntityId;
	
	@Column(nullable = false)
	private Long fromBillingEntityId;
	
	@Column(nullable = false)
	private BigDecimal amt;
	
	@Column(nullable = false)
	private BigDecimal tax;
	
	@Column(nullable = false)
	private BigDecimal totalAmt;
	
	@Column(nullable = false)
	private LocalDate date;
	
	private Long shipmentId;
	
	@OneToMany(fetch =FetchType.LAZY , mappedBy = "invoice")
	@JsonIgnore
	private List<InvoiceItem> items;
	
}
