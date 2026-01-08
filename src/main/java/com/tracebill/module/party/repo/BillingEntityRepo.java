package com.tracebill.module.party.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.party.entity.BillingEntity;

@Repository
public interface BillingEntityRepo extends JpaRepository<BillingEntity, Long> {

}
