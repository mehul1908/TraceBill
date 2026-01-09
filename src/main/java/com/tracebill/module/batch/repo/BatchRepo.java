package com.tracebill.module.batch.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tracebill.module.batch.entity.Batch;

@Repository
public interface BatchRepo extends JpaRepository<Batch, Long> {

}
