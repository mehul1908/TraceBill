package com.tracebill.module.audit.service;

import com.tracebill.module.audit.enums.AuditAction;

public interface AuditLogService {

	void create(AuditAction action, String metadata);

}
