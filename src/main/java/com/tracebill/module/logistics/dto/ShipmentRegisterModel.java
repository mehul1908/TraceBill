package com.tracebill.module.logistics.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRegisterModel {
	
	private Long toPartyId;
	
	private Long vehicleId;
	
	private List<Long> invoiceIds;
	
}
