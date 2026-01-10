package com.tracebill.module.logistics.entity;

import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "shipment_item" , 
	indexes = {@Index(name = "idx_shipment_id" , columnList = "shipment_id")}
		)

public class ShipmentItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long itemId;
	
	@ManyToOne(optional=false)
	@JoinColumn(name = "shipment_id")
	private Shipment shipment;
	
	@Column
	private Long invoiceItemId;
	
	@Column(nullable = false)
	private Long batchId;
	
	@Column(nullable = false)
	private BigInteger qty;
	
}
