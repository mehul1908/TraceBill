package com.tracebill.module.party.dto;

import com.tracebill.module.party.entity.BillingEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PartyDTO {

	private String email;
	
	private String role;
	
	private BillingEntity billingEntity;
	
	private String parentName;
}
