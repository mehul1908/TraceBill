package com.tracebill.module.production.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tracebill.module.production.sequence.ProductSequence;

@Repository
public interface ProductNoSequenceRepo extends JpaRepository<ProductSequence, Long> {

		@Modifying
	    @Query(value = "INSERT INTO product_sequence VALUES ()", nativeQuery = true)
	    void insertDummyRow();

	    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
	    Long getLastInsertedId();
}
