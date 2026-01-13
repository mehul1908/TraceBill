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
@Table(name = "prod_inv" ,
		indexes = {
				@Index(name = "idx_owner_id", columnList = "owner_id"),
		}
		)
public class ProductInventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productInvId;
	
	private Long productId;
	
	private BigInteger qty;
	
	//Party Id of owner
	private Long ownerId;

	public void subtractStock(BigInteger qty) {
		
		this.qty = this.qty.subtract(qty);
		
	}
}
