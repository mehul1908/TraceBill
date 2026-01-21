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
	private ShipmentStatus status = ShipmentStatus.CREATED;
	
	@OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ShipmentItem> items = new ArrayList<>();

}
