package com.tracebill.module.production.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactoryRegisterModel {

	@NotBlank(message = "Factory Name can not be blank")
	private String factoryName;
	
	@NotBlank(message = "Factory Address can not be blank")
	private String address;
	
}
