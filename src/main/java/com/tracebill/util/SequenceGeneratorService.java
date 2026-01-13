package com.tracebill.util;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.module.batch.repo.BatchNoSequenceRepo;
import com.tracebill.module.invoice.repo.InvoiceSequenceRepo;
import com.tracebill.module.invoice.sequence.InvoiceSequence;
import com.tracebill.module.party.enums.PartyType;
import com.tracebill.module.party.repo.PartySequenceRepo;
import com.tracebill.module.party.sequence.PartySequence;
import com.tracebill.module.production.repo.FactoryNoSequenceRepo;
import com.tracebill.module.production.repo.ProductNoSequenceRepo;

import jakarta.transaction.Transactional;

@Service
public class SequenceGeneratorService {

	@Autowired
	private BatchNoSequenceRepo batchNoRepo;
    
    @Autowired
    private ProductNoSequenceRepo prodSeqRepo;

    @Autowired
    private FactoryNoSequenceRepo factSeqRepo;
    
    @Autowired
    private PartySequenceRepo partySeqRepo;
    
    @Autowired
    private InvoiceSequenceRepo invoiceSeqRepo;
    
    @Autowired
    private FinancialYearUtil fyUtil;

    @Transactional
    public synchronized long nextProductSeq() {
        prodSeqRepo.insertDummyRow();
        return prodSeqRepo.getLastInsertedId();
    }

    @Transactional
    public synchronized long nextFactorySeq() {
        factSeqRepo.insertDummyRow();
        return factSeqRepo.getLastInsertedId();
    }
    
    @Transactional
    public synchronized int nextBatchSeq(Long productId, Long factoryId, LocalDate manufactureDate) {
        int nextSeq = batchNoRepo.findMaxSeq(manufactureDate, productId, factoryId) + 1;
        batchNoRepo.insertSeq(manufactureDate, productId, factoryId, nextSeq);
        return nextSeq;
    }
    
    @Transactional
    public synchronized String getPartyCode(PartyType type) {
    	PartySequence seq = partySeqRepo
                .findByPartyType(type)
                .orElseGet(() -> PartySequence.builder()
                    .partyType(type)
                    .lastSeq(0L)
                    .build()
                );

            long next = seq.getLastSeq() + 1;
            seq.setLastSeq(next);
            partySeqRepo.save(seq);

            return String.format(
                "%s%03d",
                type.getPrefix(),
                next
            );
    }
    
    
    public String generateInvoiceNo(Long partyId, String partyCode, LocalDate invoiceDate) {

        String fy = fyUtil.resolve(invoiceDate);

        InvoiceSequence seq = invoiceSeqRepo
            .findByPartyIdAndFinancialYearForUpdate(partyId, fy)
            .orElseGet(() -> InvoiceSequence.builder()
                .partyId(partyId)
                .financialYear(fy)
                .lastSeq(0L)
                .build()
            );

        long next = seq.getLastSeq() + 1;
        seq.setLastSeq(next);
        invoiceSeqRepo.save(seq);

        return String.format(
            "INV/%s/%s/%06d",
            partyCode,
            fy,
            next
        );
    }
    
}


