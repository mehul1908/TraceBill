package com.tracebill.module.blockchain.entity;

import java.time.LocalDateTime;

import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;
import com.tracebill.module.blockchain.enums.BlockchainIntentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(
    name = "blockchain_tx_intents",
    indexes = {
        @Index(name = "idx_intent_type", columnList = "intentType"),
        @Index(name = "idx_reference", columnList = "referenceType, referenceId")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlockchainIntentType intentType;

    // SHIPMENT / INVOICE / BATCH / PRODUCT
    @Column(nullable = false)
    private String referenceType;

    // shipmentId / invoiceId / batchId / productId
    @Column(nullable = false)
    private Long referenceId;

    // hash of off-chain payload
    @Column(nullable = false, length = 66)
    private String dataHash;

    @Column(nullable = false)
    private Long fromPartyId;

    @Column(nullable = false)
    private Long toPartyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BlockchainIntentStatus status = BlockchainIntentStatus.PENDING;

    private String failureReason;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

