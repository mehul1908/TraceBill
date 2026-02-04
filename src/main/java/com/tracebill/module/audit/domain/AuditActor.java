package com.tracebill.module.audit.domain;

import lombok.Value;

@Value
public class AuditActor {

    Long actorId;
    ActorType type;

    public static AuditActor user(Long userId) {
        return new AuditActor(userId, ActorType.USER);
    }

    public static AuditActor system() {
        return new AuditActor(null, ActorType.SYSTEM);
    }

    public enum ActorType {
        USER,
        SYSTEM
    }
}
