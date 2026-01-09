package com.tracebill.module.blockchain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "blockchain_transactions",
    indexes = {
        @Index(name = "idx_tx_hash", columnList = "txHash"),
        @Index(name = "idx_intent_id", columnList = "intentId")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Logical FK, no JPA dependency
    @Column(nullable = false)
    private Long intentId;

    @Column(nullable = false, unique = true, length = 66)
    private String txHash;

    @Column(nullable = false)
    private Long blockNumber;

    @Column(nullable = false)
    private String contractAddress;

    @Column(nullable = false)
    private String submittedWallet;

    private String eventName;

    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();

    private LocalDateTime confirmedAt;
}
