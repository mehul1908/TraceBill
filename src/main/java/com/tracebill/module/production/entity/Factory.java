package com.tracebill.module.production.entity;

import jakarta.persistence.Column;
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
@Table(name = "facory" ,
indexes = {
		@Index(name = "idx_fact_code", columnList = "fact_code"),
}
	)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Factory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long factoryId;
	
	@Column(nullable=false , unique = true)
	private String factCode;
	
	@Column(nullable = false)
	private String factoryName;
	
	@Column(nullable = false)
	private String address;
	
	@Column(nullable = false)
	private Long manufacturer_id;

}
