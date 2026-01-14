package com.tracebill.module.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.invoice.dto.InvoiceRegisterModel;
import com.tracebill.module.invoice.service.InvoiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createInvoice(@RequestBody @Valid InvoiceRegisterModel model){
		
		String invoiceNo = invoiceService.generateInvoice(model);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, invoiceNo, "Invoice Created successfully"));
		
	}
	
}
