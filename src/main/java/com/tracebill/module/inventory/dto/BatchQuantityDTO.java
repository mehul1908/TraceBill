package com.tracebill.module.inventory.dto;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BatchQuantityDTO {

	private Long batchId;
	
	private BigInteger quantity;
	
}
