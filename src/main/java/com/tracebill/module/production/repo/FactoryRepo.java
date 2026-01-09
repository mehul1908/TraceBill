package com.tracebill.module.production.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.production.entity.Factory;

@Repository
public interface FactoryRepo extends JpaRepository<Factory, Long> {

}
