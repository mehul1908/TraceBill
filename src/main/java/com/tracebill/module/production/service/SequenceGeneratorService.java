package com.tracebill.module.production.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.module.production.repo.FactoryNoSequenceRepo;
import com.tracebill.module.production.repo.ProductNoSequenceRepo;

import jakarta.transaction.Transactional;

@Service
public class SequenceGeneratorService {

    
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
}


