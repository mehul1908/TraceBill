package com.tracebill.module.inventory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.inventory.entity.InventoryTransaction;

@Repository
public interface InventoryTxnRepo extends JpaRepository<InventoryTransaction, Long>{

}
