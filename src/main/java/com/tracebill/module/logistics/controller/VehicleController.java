package com.tracebill.module.logistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.logistics.dto.VehicleRegisterModel;
import com.tracebill.module.logistics.service.VehicleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {
	
	@Autowired
	private VehicleService vehicleService;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createVehicle(@RequestBody @Valid VehicleRegisterModel model){
		String vehicleNo = vehicleService.createVehicle(model);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true, vehicleNo, "Vehicle created successfully"));
	}
	
}
