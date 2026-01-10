package com.tracebill.module.production.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.production.dto.ProductRegisterModel;
import com.tracebill.module.production.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService prodService;
	
	public ResponseEntity<ApiResponse> createProduct(@RequestBody @Valid ProductRegisterModel model){
		Long productId = prodService.createProduct(model);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, productId, "Product Created"));
	}
	
}
