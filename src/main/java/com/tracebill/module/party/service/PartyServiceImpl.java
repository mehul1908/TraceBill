package com.tracebill.module.party.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.enums.PartyType;
import com.tracebill.module.party.exception.PartyNotFoundException;
import com.tracebill.module.party.repo.PartyRepo;
import com.tracebill.util.SequenceGeneratorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService {


	private final PartyRepo partyRepo;
	
	private final SequenceGeneratorService sequenceGenerator;

	@Override
	public Long createParty(String email, Long parentPartyId, PartyType partyType) {
		
		if((partyType != PartyType.MANUFACTURER && partyType !=PartyType.TRANSPORTER)&& parentPartyId == null) {
			throw new IllegalArgumentException("Parent Id can not be null");

		}
		
		if(parentPartyId!= null && !this.existById(parentPartyId)) {
			throw new IllegalArgumentException("Parent Id does not exist.");
		}
		
		String partyCode = sequenceGenerator.getPartyCode(partyType);
		
		Party party = Party.builder()
				.email(email)
				.parentPartyId(parentPartyId)
				.type(partyType)
				.partyCode(partyCode)
				.build();
		partyRepo.save(party);
		return party.getPartyId();
	}

	@Override
	public boolean existById(Long parentPartyId) {
		return partyRepo.existsById(parentPartyId);
	}

	@Override
	public Long getPartyIdByEmail(String email) {
		Optional<Party> partyOp = partyRepo.findByEmail(email);
		if(partyOp.isEmpty())
			throw new PartyNotFoundException(email);
		return partyOp.get().getPartyId();
	}

	@Override
	public Party getPartyById(Long manufacturerId) {
		return partyRepo.findById(manufacturerId)
				.orElseThrow(() -> new PartyNotFoundException(manufacturerId));
	}

	@Override
	public Party getPartyByEmail(String email) {
		return partyRepo.findByEmail(email)
				.orElseThrow(() -> new PartyNotFoundException(email));
	}

}
