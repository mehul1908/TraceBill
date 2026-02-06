package com.tracebill.module.production.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
@Table(name = "product" ,
indexes = {
		@Index(name = "idx_prod_code", columnList = "prod_code"),
}
	)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productId;
	
	@Column(name = "name" , nullable = false , unique = true)
	private String prodName;
	
	private String prodCode;
	
	@Column(name = "hsnCode" , nullable = false , unique = true)
	private String hsnCode;
	
	@Column(nullable = false)
	private BigDecimal mrp;
	
	@Builder.Default
	private List<BigDecimal> defaultRate = new ArrayList<>(3);
	
	@Column(nullable=false)
	private BigDecimal gstRate;
	
	@Column(nullable=false)
	private BigDecimal cessRate;
	
	private String productHash;
	
	@Version
	private Long version;
	
	
}
