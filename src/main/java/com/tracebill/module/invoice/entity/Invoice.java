package com.tracebill.module.invoice.entity;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tracebill.module.invoice.enums.InvoiceStatus;
import com.tracebill.module.logistics.entity.ShipmentItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	@OneToMany(fetch =FetchType.EAGER , mappedBy = "invoice")
	@JsonIgnore
	private List<InvoiceItem> items;
	
	@Enumerated(EnumType.STRING)
	private InvoiceStatus status;
	
	@JsonIgnore
	public String computeHash() {
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");

	        StringBuilder payload = new StringBuilder();

	        // ---- shipment level ----
	        payload.append("invoiceid=").append(shipmentId).append("|");
	        payload.append("fromBillingEntityId=").append(fromBillingEntityId).append("|");
	        payload.append("toBillingEntityId=").append(toBillingEntityId).append("|");
	        payload.append("date=").append(date.toString()).append("|");
	        payload.append("amt=").append(amt.toString()).append("|");

	        // ---- items (sorted for determinism) ----
	        items.stream()
	                .sorted(Comparator
	                        .comparing(InvoiceItem::getBatchId)
	                        .thenComparing(InvoiceItem::getProductId))
	                .forEach(item -> {
	                    payload.append("item[");
	                    payload.append("productId=").append(item.getProductId()).append(",");
	                    payload.append("batchId=").append(item.getBatchId()).append(",");
	                    payload.append("qty=").append(item.getQty()).append(",");
	                    payload.append("]|");
	                });

	        byte[] hashBytes = digest.digest(
	                payload.toString().getBytes(StandardCharsets.UTF_8)
	        );

	        return "0x" + HexFormat.of().formatHex(hashBytes);

	    } catch (Exception ex) {
	        throw new IllegalStateException("Failed to compute shipment hash", ex);
	    }
	}

	
}
