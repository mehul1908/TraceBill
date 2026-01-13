package com.tracebill.module.party.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.tracebill.module.party.enums.PartyType;
import com.tracebill.module.party.sequence.PartySequence;

import jakarta.persistence.LockModeType;

public interface PartySequenceRepo extends JpaRepository<PartySequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PartySequence> findByPartyType(PartyType partyType);
}

