package com.tracebill.module.batch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.batch.dto.BatchRegisterModel;
import com.tracebill.module.batch.service.BatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/batch")
public class BatchController {

	@Autowired
	private BatchService batchService;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createBatch(@RequestBody @Valid BatchRegisterModel model){
		
		String batchNo = batchService.createBatch(model);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, batchNo , "Batch is created successfully"));
		
	}
	
}
