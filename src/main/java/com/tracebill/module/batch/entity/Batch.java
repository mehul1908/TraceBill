package com.tracebill.module.batch.entity;

import java.math.BigInteger;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "batch" ,
	indexes = {
			@Index(name = "idx_batch_no", columnList = "batch_no"),
	}
		)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Batch {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long batchId;
	
	@Column(unique = true , nullable = false)
	private String batchNo;
	
	@Column(nullable  = false)
	private LocalDate manufacturedDate;
	
	@Column(nullable = false)
	private LocalDate expiryDate;
	
	@Column(nullable = false)
	private Long factoryId;
	
	@Column(nullable = false)
	private Long productId;
	
	@Column(nullable=false)
	private BigInteger manufacturedQty;
	
	@Version
	private Long version;
	
	
	private String batchHash;
	
	
}
