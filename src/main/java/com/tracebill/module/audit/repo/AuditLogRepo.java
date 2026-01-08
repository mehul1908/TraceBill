package com.tracebill.module.audit.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tracebill.module.audit.entity.AuditLog;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long>{

}
