package com.tracebill.module.blockchain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.blockchain.entity.BlockchainTransaction;


@Repository
public interface BlockchainTxnRepo extends JpaRepository<BlockchainTransaction, Long> {

}
