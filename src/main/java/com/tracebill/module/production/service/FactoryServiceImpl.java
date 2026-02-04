package com.tracebill.module.production.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.exception.UnauthorizedUserException;
import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.audit.service.AuditLogService;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.party.entity.Party;
import com.tracebill.module.party.enums.PartyType;
import com.tracebill.module.party.service.PartyService;
import com.tracebill.module.production.dto.FactoryRegisterModel;
import com.tracebill.module.production.entity.Factory;
import com.tracebill.module.production.repo.FactoryRepo;
import com.tracebill.util.SequenceGeneratorService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FactoryServiceImpl implements FactoryService{

	@Autowired
	private FactoryRepo factoryRepo;
	
	@Autowired
	private SequenceGeneratorService sequenceGenerator;
	
	@Autowired
	private AuthenticatedUserProvider authenticatedUser;
	
	@Autowired
	private PartyService partyService;
	
	@Autowired
	private AuditLogService auditService;
	
	@Override
	@Transactional
	public Long createFactory(@Valid FactoryRegisterModel model) {
		
		Long manufacturerPartyId = authenticatedUser.getAuthenticatedParty();
		
		Party manufacturer = partyService.getPartyById(manufacturerPartyId);

	    if (manufacturer.getType() != PartyType.MANUFACTURER) {
	        throw new UnauthorizedUserException("Only manufacturers can create factories");
	    }

	    long seq = sequenceGenerator.nextFactorySeq();
	    String factoryCode = String.format("F%03d", seq);


	    Factory factory = Factory.builder()
	        .factCode(factoryCode)
	        .factoryName(model.getFactoryName())
	        .address(model.getAddress())
	        .manufacturerPartyId(manufacturerPartyId)
	        .build();

	    factoryRepo.save(factory);
	    log.info("Factory {} created successfully", factoryCode);
		auditService.create(AuditAction.CREATED, "Factory Created : "+factoryCode);
	    return factory.getFactoryId();
	}

	@Override
	public boolean existById(Long factoryId) {
		return factoryRepo.existsById(factoryId);
	}

	@Override
	public boolean isFactoryOwnedBy(Long factoryId, Long partyId) {
		return factoryRepo.existsByFactoryIdAndManufacturerPartyId(factoryId , partyId);
	}

}
