package com.tracebill.module.party.service;

import com.tracebill.module.party.dto.BillingEntityRegisterModel;

import jakarta.validation.Valid;

public interface BillingEntityService {

	void createBillingEntity(@Valid BillingEntityRegisterModel model);

}
