package com.tracebill.module.invoice.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRegisterModel {

	@NotNull(message = "From Id can not be null")
	private Long fromPartyId;
	
	private LocalDate date;
	
	@NotNull(message = "Item can not be null")
	@Size(min = 1 , message="There will be atleast one item")
	private List<InvoiceItemRegisterModel> items;
	
}
