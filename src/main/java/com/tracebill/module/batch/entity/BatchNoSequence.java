package com.tracebill.module.batch.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "batch_no_sequence",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "manufacture_date",
                "product_id",
                "factory_id",
                "seq"
            }
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchNoSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "manufacture_date", nullable = false)
    private LocalDate manufactureDate;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "factory_id", nullable = false)
    private Long factoryId;

    @Column(nullable = false)
    private Integer seq;
}
