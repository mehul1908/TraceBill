package com.tracebill.module.logistics.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "vehicle" , 
	indexes = {@Index(name = "idx_vehicle_no" , columnList = "vehicle_no")}
)
public class Vehicle {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long vehicleId;
	
	@Column(nullable = false , unique = true)
	private String vehicleNo;
	
	@Column(nullable = false)
	private Long tranporterId;
	
	@Column(nullable = false)
	private BigDecimal capacity; // in kg
	
}
