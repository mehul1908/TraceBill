package com.tracebill.module.invoice.sequence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "invoice_sequences",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"partyId", "financialYear"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long partyId;

    @Column(length = 7, nullable = false)
    private String financialYear; // 24-25

    @Column(nullable = false)
    private Long lastSeq;
}

