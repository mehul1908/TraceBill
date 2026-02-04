package com.tracebill.module.audit.domain;

import com.tracebill.module.audit.enums.AuditAction;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuditCommand {

    AuditAction action;
    AuditActor actor;
    String metadata;
    String correlationId; // replay / blockchain tx hash / request id
}
