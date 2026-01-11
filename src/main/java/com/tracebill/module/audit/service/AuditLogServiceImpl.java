package com.tracebill.module.audit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tracebill.module.audit.entity.AuditLog;
import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.audit.repo.AuditLogRepo;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService{

	private final AuditLogRepo auditRepo;
	
	private final AuthenticatedUserProvider authenticatedUser;
	
	@Override
	@Transactional
	public void create(AuditAction action, String metadata) {
		Long performedBy = authenticatedUser.getAuthenticatedParty();
		System.out.println("performed by "  + performedBy);
		AuditLog audit = AuditLog.builder()
				.action(action)
				.performedBy(performedBy)
				.metadata(metadata)
				.build();
		auditRepo.save(audit);
		
		log.info("Audit saved : " + audit.getId());
	}

}
