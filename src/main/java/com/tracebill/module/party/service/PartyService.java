package com.tracebill.module.party.service;


import com.tracebill.module.party.enums.PartyType;

public interface PartyService {

	Long createParty(String email, Long parentPartyId, PartyType partyType);

	boolean existById(Long parentPartyId);

	Long getPartyIdByEmail(String email);


}
