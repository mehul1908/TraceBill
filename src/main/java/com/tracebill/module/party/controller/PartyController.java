package com.tracebill.module.party.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.party.dto.BillingEntityRegisterModel;
import com.tracebill.module.party.service.BillingEntityService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/party")
public class PartyController {

	@Autowired
	private BillingEntityService billingEntityService;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createBillingEntity(@RequestBody @Valid BillingEntityRegisterModel model){
		
		billingEntityService.createBillingEntity(model);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, null , "Billing Entity Created Successfully"));
		
		
	}
	
}
