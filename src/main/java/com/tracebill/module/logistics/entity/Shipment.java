package com.tracebill.module.logistics.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tracebill.module.logistics.enums.ShipmentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.HexFormat;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shipmentId;
	
	@Column(nullable=false)
	private Long toPartyId;
	
	@Column(nullable=false)
	private Long fromPartyId;
	
	@Column(nullable=false)
	private Long vehicleId;
	
	private LocalDateTime dispatchTime;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(length = 25)
	private ShipmentStatus status = ShipmentStatus.CREATED;
	
	@OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.EAGER)
	@Builder.Default
	private List<ShipmentItem> items = new ArrayList<>();
	
	

	@JsonIgnore
	public String computeHash() {
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");

	        StringBuilder payload = new StringBuilder();

	        // ---- shipment level ----
	        payload.append("shipmentId=").append(shipmentId).append("|");
	        payload.append("fromPartyId=").append(fromPartyId).append("|");
	        payload.append("toPartyId=").append(toPartyId).append("|");
	        payload.append("vehicleId=").append(vehicleId).append("|");

	        // ---- items (sorted for determinism) ----
	        items.stream()
	                .sorted(Comparator
	                        .comparing(ShipmentItem::getBatchId)
	                        .thenComparing(ShipmentItem::getProductId))
	                .forEach(item -> {
	                    payload.append("item[");
	                    payload.append("productId=").append(item.getProductId()).append(",");
	                    payload.append("batchId=").append(item.getBatchId()).append(",");
	                    payload.append("qty=").append(item.getQty()).append(",");
	                    payload.append("invoiceItemId=")
	                           .append(item.getInvoiceItemId() == null ? "null" : item.getInvoiceItemId());
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
