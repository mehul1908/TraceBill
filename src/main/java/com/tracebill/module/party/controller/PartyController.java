package com.tracebill.module.party.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracebill.dto.ApiResponse;
import com.tracebill.module.party.dto.PartyDTO;
import com.tracebill.module.party.dto.PartyRegisterModel;
import com.tracebill.module.party.service.PartyService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/party")
public class PartyController {

	@Autowired
    private PartyService partyService;
	
	@PostMapping("/")
	public ResponseEntity<ApiResponse> createParty(@RequestBody @Valid PartyRegisterModel model){
		
		PartyDTO party = partyService.createParty(model);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(true,party , "Party Created Successfully"));
		
		
	}
	
}
