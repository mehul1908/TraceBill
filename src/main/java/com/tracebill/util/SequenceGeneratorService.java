package com.tracebill.util;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.module.batch.repo.BatchNoSequenceRepo;
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
}


