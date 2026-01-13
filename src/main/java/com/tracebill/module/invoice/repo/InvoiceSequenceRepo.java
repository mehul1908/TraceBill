package com.tracebill.module.invoice.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tracebill.module.invoice.sequence.InvoiceSequence;

import jakarta.persistence.LockModeType;

public interface InvoiceSequenceRepo extends JpaRepository<InvoiceSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s FROM InvoiceSequence s
        WHERE s.partyId = :partyId
          AND s.financialYear = :fy
    """)
    Optional<InvoiceSequence> findByPartyIdAndFinancialYearForUpdate(
        Long partyId,
        String fy
    );
}

