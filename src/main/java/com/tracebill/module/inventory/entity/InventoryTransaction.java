package com.tracebill.module.inventory.entity;

import java.math.BigInteger;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long invTxnId;
	
	@Column(nullable = false)
	private Long performedOn;
	
	@Column(nullable = false)
	private Boolean increment;
	
	@Column(nullable = false)
	private Long productId;
	
	@Column(nullable = false)
	private Long batchId;
	
	@Column(nullable = false)
	private BigInteger qty;
	
	private String refType;
	
	private String refNo;
	
	@Builder.Default
	private LocalDateTime txnDate = LocalDateTime.now();
}
