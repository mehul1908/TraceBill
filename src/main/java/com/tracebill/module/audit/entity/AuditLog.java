package com.tracebill.module.audit.entity;

import java.time.LocalDateTime;

import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private AuditAction action;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(nullable=false)
    private User performedBy;

    @Builder.Default
    private LocalDateTime performedAt = LocalDateTime.now();

    private String metadata;
}

