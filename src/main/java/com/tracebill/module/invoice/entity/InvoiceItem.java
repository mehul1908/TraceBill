package com.tracebill.module.invoice.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.HexFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tracebill.module.logistics.entity.ShipmentItem;

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
	
	@Column(nullable = false)
	private BigDecimal taxRate;
	
	@Builder.Default
	private BigDecimal disc = new BigDecimal(0);

	@JsonIgnore
	public String computeHash() {
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");

	        StringBuilder payload = new StringBuilder();

	        // ---- shipment level ----
	        payload.append("invoiceItemId=").append(invoiceItemId).append("|");
	        payload.append("qty=").append(qty).append("|");
	        payload.append("rate=").append(rate).append("|");
	        payload.append("disc=").append(disc).append("|");

	        byte[] hashBytes = digest.digest(
	                payload.toString().getBytes(StandardCharsets.UTF_8)
	        );

	        return "0x" + HexFormat.of().formatHex(hashBytes);

	    } catch (Exception ex) {
	        throw new IllegalStateException("Failed to compute shipment hash", ex);
	    }
	}
	
}
