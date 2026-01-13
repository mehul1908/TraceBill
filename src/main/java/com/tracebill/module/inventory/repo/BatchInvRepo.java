package com.tracebill.module.inventory.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tracebill.module.inventory.entity.BatchInventory;

@Repository
public interface BatchInvRepo extends JpaRepository<BatchInventory, Long>{

	@Query("""
		    SELECT bi
		    FROM BatchInventory bi
		    JOIN Batch b ON b.batchId = bi.batchId
		    WHERE bi.ownerId = :ownerId
		      AND b.productId = :productId
		      AND bi.qty > 0
		    ORDER BY b.manufacturedDate ASC
		""")
		List<BatchInventory> findAvailableBatchesFIFO(
		        @Param("productId") Long productId,
		        @Param("ownerId") Long ownerId
		);

	
}
