package com.tracebill.module.party.service;


import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.enums.PartyType;

public interface PartyService {

	Long createParty(String email, Long parentPartyId, PartyType partyType);

	boolean existById(Long parentPartyId);

	Long getPartyIdByEmail(String email);

	Party getPartyById(Long manufacturerId);

	Party getPartyByEmail(String email);


}
