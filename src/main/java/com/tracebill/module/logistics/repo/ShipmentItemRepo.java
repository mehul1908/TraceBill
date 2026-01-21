package com.tracebill.module.logistics.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.logistics.entity.ShipmentItem;

@Repository
public interface ShipmentItemRepo extends JpaRepository<ShipmentItem, Long> {

}
