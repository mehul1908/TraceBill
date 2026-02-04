package com.tracebill.module.blockchain.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.blockchain.entity.BlockchainIntent;
import com.tracebill.module.blockchain.enums.BlockchainIntentStatus;

@Repository
public interface BlockchainIntentRepo extends JpaRepository<BlockchainIntent, Long> {

	Optional<BlockchainIntent> findByDataHash(String txHash);

	List<BlockchainIntent> findTop20ByStatusOrderByCreatedAt(BlockchainIntentStatus pending);

}
