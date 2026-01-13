package com.tracebill.module.invoice.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemRegisterModel {

	@NotNull(message ="Product Id can not be null" )
	private Long productId;
	
	@Min(value = 1 , message = "Quantity must be greater than 0")
	@NotNull(message = "Quantity can not be null")
	private BigInteger qty;
	
	@Min(value = 0 , message = "Rate must be atleast 0")
	@NotNull(message = "Rate can not be null")
	private BigDecimal rate;
	
	private BigDecimal disc;
	
}
