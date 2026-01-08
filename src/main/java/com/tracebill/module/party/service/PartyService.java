package com.tracebill.module.party.service;

import java.util.Optional;

import com.tracebill.module.party.dto.PartyDTO;
import com.tracebill.module.party.dto.PartyRegisterModel;
import com.tracebill.module.party.entity.Party;

import jakarta.validation.Valid;

public interface PartyService {

	PartyDTO createParty(@Valid PartyRegisterModel model);

	Optional<Party> getPartyOpByEmailId(String email);

}
