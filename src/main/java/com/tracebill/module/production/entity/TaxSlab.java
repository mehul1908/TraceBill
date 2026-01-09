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
@Table(name = "tax_slab",
	indexes = {
			@Index(name = "index_code" , columnList = "code")
	}
		)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxSlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // GST_5, GST_12, GST_18

    @Column(nullable = false)
    private Double cgst;

    @Column(nullable = false)
    private Double sgst;

    @Column(nullable = false)
    private Double igst;
    
    @Column(nullable = false)
    private Double cess;

    @Column(nullable = false)
    private boolean active;
}

