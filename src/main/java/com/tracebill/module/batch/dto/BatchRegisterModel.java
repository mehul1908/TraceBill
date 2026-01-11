package com.tracebill.module.batch.dto;

import java.math.BigInteger;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchRegisterModel {

	@NotNull(message = "Manufacture Date can not be null")
	private LocalDate manufacturedDate;
	
	@NotNull(message = "Expiry Date can not be null")
	private LocalDate expiryDate;
	
	@NotNull(message = "Factory Id can not be null")
	private Long factoryId;
	
	@NotNull(message = "Product Id can not be null")
	private Long productId;
	
	@NotNull(message = "Manufactured Quantity can not be null")
	@Positive(message = "Quantity can not be negative or null")
	private BigInteger manufacturedQty;
	
}
