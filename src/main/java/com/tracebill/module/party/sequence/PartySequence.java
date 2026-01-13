package com.tracebill.module.party.sequence;

import com.tracebill.module.party.enums.PartyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    name = "party_sequences",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "partyType")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartySequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyType partyType;

    @Column(nullable = false)
    private Long lastSeq;
}

