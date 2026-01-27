package com.tracebill.module.logistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.logistics.dto.ShipmentRegisterModel;
import com.tracebill.module.logistics.service.ShipmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/shipment")
public class ShipmentController {

	@Autowired
	private ShipmentService shipmentService;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createShipment(@RequestBody @Valid ShipmentRegisterModel model){
		Long shipmentId = shipmentService.createShipment(model);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, shipmentId, "Shipment Created successfully"));
	}
	
	@PostMapping("/{shipmentId}/dispatch")
	public ResponseEntity<ApiResponse> dispatchShipment(@PathVariable Long shipmentId){
		Long savedShipmentId = shipmentService.dispatchShipment(shipmentId);
		return ResponseEntity.ok(new ApiResponse(true, savedShipmentId, "Shipment is dispatched"));
	}
	
	@PostMapping("/{shipmentId}/receive")
	public ResponseEntity<ApiResponse> receiveShipment(@PathVariable Long shipmentId){
		Long savedShipmentId = shipmentService.receiveShipment(shipmentId);
		return ResponseEntity.ok(new ApiResponse(true, savedShipmentId, "Shipment is received"));
	}
	
	@PostMapping("/{shipmentId}/cancel")
	public ResponseEntity<ApiResponse> cancelShipment(@PathVariable Long shipmentId){
		Long savedShipmentId = shipmentService.cancelShipment(shipmentId);
		return ResponseEntity.ok(new ApiResponse(true, savedShipmentId, "Shipment is cancelled"));
	}
	
}
