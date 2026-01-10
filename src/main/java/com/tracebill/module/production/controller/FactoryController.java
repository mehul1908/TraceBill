package com.tracebill.module.production.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.production.dto.FactoryRegisterModel;
import com.tracebill.module.production.service.FactoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/factory")
public class FactoryController {

	@Autowired
	private FactoryService factoryService;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createFactory(@RequestBody @Valid FactoryRegisterModel model){
		
		Long factoryId =  factoryService.createFactory(model);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true , factoryId , "Factory created successfully."));
		
	}
	
}
