package com.tracebill.module.inventory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.inventory.entity.ProductInventory;

@Repository
public interface ProductInvRepo extends JpaRepository<ProductInventory, Long>{

}
