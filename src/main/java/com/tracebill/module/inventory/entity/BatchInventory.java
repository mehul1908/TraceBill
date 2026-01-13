package com.tracebill.module.inventory.entity;

import java.math.BigInteger;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "batch_inv" ,
		indexes = {
				@Index(name = "idx_owner_id", columnList = "owner_id"),
		}
		)
public class BatchInventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long batchInvId;
	
	private Long batchId;
	
	private BigInteger qty;
	
	//Party Id of owner
	private Long ownerId;

	public void subtractStock(BigInteger remainingQty) {
		
		 this.qty = this.qty.subtract(remainingQty);
		
	}
}
