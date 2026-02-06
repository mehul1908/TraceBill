package com.tracebill.module.party.service;

import com.tracebill.module.party.dto.BillingEntityRegisterModel;
import com.tracebill.module.party.entity.BillingEntity;

import jakarta.validation.Valid;

public interface BillingEntityService {

	void createBillingEntity(@Valid BillingEntityRegisterModel model);

	BillingEntity getBillingEntityByPartyId(Long partyId);

	BillingEntity getBillingEntityByBillingEntityId(Long toBillingEntityId);

}
