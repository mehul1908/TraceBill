package com.tracebill.module.audit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tracebill.module.audit.domain.AuditActor.ActorType;
import com.tracebill.module.audit.domain.AuditCommand;
import com.tracebill.module.audit.entity.AuditLog;
import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.audit.repo.AuditLogRepo;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepo auditRepo;
    private final AuthenticatedUserProvider authenticatedUser;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(AuditAction action, String metadata) {

        Long actorId = authenticatedUser.getAuthenticatedParty();

        AuditLog audit = new AuditLog(
                action,
                actorId,
                ActorType.USER,
                metadata,
                null
        );

        auditRepo.save(audit);

        log.info(
            "Audit recorded: action={}, actorId={}, auditId={}",
            action,
            actorId,
            audit.getId()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSystem(
            AuditAction action,
            String metadata,
            String correlationId
    ) {
        AuditLog audit = new AuditLog(
                action,
                null,
                ActorType.SYSTEM,
                metadata,
                correlationId
        );

        auditRepo.save(audit);

        log.info(
            "System audit recorded: action={}, correlationId={}, auditId={}",
            action,
            correlationId,
            audit.getId()
        );
    }
}
