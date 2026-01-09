package com.tracebill.module.logistics.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.logistics.entity.Shipment;

@Repository
public interface ShipmentRepo extends JpaRepository<Shipment, Long> {

}
