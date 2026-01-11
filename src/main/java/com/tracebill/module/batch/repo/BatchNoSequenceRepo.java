package com.tracebill.module.batch.repo;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tracebill.module.batch.entity.BatchNoSequence;


@Repository
public interface BatchNoSequenceRepo extends JpaRepository<BatchNoSequence, Long> {

    @Query(value = """
        SELECT COALESCE(MAX(seq), 0)
        FROM batch_no_sequence
        WHERE manufacture_date = :date
          AND product_id = :productId
          AND factory_id = :factoryId
        """, nativeQuery = true)
    int findMaxSeq(
        @Param("date") LocalDate date,
        @Param("productId") Long productId,
        @Param("factoryId") Long factoryId
    );

    @Modifying
    @Query(value = """
        INSERT INTO batch_no_sequence
        (manufacture_date, product_id, factory_id, seq)
        VALUES (:date, :productId, :factoryId, :seq)
        """, nativeQuery = true)
    void insertSeq(
        @Param("date") LocalDate date,
        @Param("productId") Long productId,
        @Param("factoryId") Long factoryId,
        @Param("seq") int seq
    );
}
