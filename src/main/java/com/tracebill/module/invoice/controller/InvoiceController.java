package com.tracebill.module.invoice.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.invoice.dto.InvoiceRegisterModel;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.service.InvoiceService;
import com.tracebill.util.InvoicePDFGenerator;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private InvoicePDFGenerator invoicePDFGenerator;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createInvoice(@RequestBody @Valid InvoiceRegisterModel model){
		
		String invoiceNo = invoiceService.generateInvoice(model);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, invoiceNo, "Invoice Created successfully"));
		
	}
	
	@PostMapping("/{invoiceId}/cancel")
	public ResponseEntity<ApiResponse> cancelInvoice(@PathVariable Long invoiceId){
		
		invoiceService.cancelInvoice(invoiceId);
		return ResponseEntity.ok(new ApiResponse(true, invoiceId, "Invoice is cancelled successfully"));
		
	}
	
	@GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> viewInvoicePdf(@PathVariable Long invoiceId) {

        Invoice invoice = invoiceService.getInvoiceById(invoiceId);

        byte[] pdfBytes = invoicePDFGenerator.createInvoicePDF(invoice);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Important: inline shows PDF in Postman / browser
        headers.set(
            HttpHeaders.CONTENT_DISPOSITION,
            "inline; filename=invoice-" + invoice.getInvoiceNo() + ".pdf"
        );

        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
	
}
